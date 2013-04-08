package fengfei.forest.slice.config.zk;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.zk.ZkBerainClient;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.Config.ResConfig;
import fengfei.forest.slice.config.Config.RouterConfig;
import fengfei.forest.slice.config.Config.SliceConfig;
import fengfei.forest.slice.config.SliceReader;
import fengfei.forest.slice.exception.ConfigException;
import fengfei.forest.slice.utils.ResourcesUtils;

public class SliceConfigReader implements SliceReader<Config> {

	static Logger log = LoggerFactory.getLogger(SliceConfigReader.class);
	//
	static final String FOREST_ROOT_PATH = "/routers";
	//
	static final String FOREST_ROUTER_CLASS = "/router_class";
	static final String FOREST_EQUALIZER_CLASS = "/eq_class";
	static final String FOREST_PLOTTER_CLASS = "/plotter_class";
	static final String FOREST_PARENT_ID = "/parent_id";
	static final String FOREST_PARENT_PATH = "/parent_path";
	static final String FOREST_OVERFLOW = "/overflow";

	static final String FOREST_RESOURCES_PATH = "/resources";
	static final String FOREST_EXTRA_INFO_PATH = "/extra_infos";
	static final String FOREST_SLICES_PATH = "/slices";
	static final String S_SUB_SLICES_PATH = "/sub_slices";

	//
	static final String KEY_HOST = "host";
	static final String KEY_PORT = "port";
	static final String KEY_WEIGHT = "weight";
	static final String KEY_SCHEMA = "schema";
	//
	static final String S_ALIAS = "alias";
	static final String S_FUNCTION = "func";
	static final String S_RESOURCE_NAME = "res_name";
	static final String S_READ = "read";
	static final String S_WRITE = "write";
	static final String S_READ_WRITE = "readwrite";
	static final String S_RANGE = "range";
	static final String S_SUB_ROUTER_ID = "sub_id";
	static final String S_SUB_ROUTER_PATH = "sub_path";
	//
	static final String SPLIT_REG = "&|,|\\||，|	|\n|\r|\n\r";
	static final String SPLIT_REG_BOUND = "~|-|－|——|—";
	ZkBerainClient client;
	String namespace = "";

	public SliceConfigReader() {

		try {
			Properties properties = ResourcesUtils
					.getResourceAsProperties("zk.properties");
			init(properties);
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	public SliceConfigReader(String zkProperties) {

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(zkProperties));
			init(properties);
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	void init(Properties properties) {
		client = new ZkBerainClient(properties);
		client.start();
		namespace = client.getClient().getNamespace();
	}

	public static void main(String[] args) {
		SliceConfigReader reader = new SliceConfigReader();

		Config config = reader.read("/main");
		System.out.println(config);
	}

	/**
	 * *
	 * 
	 * <pre>
	 * -root_path - routers - router_parent01 - r01 - r02 - root_path - routers
	 * 		- router_parent01 - r01 - r02
	 * 
	 * 
	 * </pre>
	 */
	@Override
	public Config read(String rootPath) throws ConfigException {
		Config config = new Config();

		config.setPath(rootPath + FOREST_ROOT_PATH);

		try {
			check(config.path);
			List<BerainEntry> routerEntries = client.nextChildren(config.path);
			for (BerainEntry entry : routerEntries) {
				System.out.println("====" + entry);
				RouterConfig routerConfig = readRouterConfig(entry);
				System.out.println("====" + routerConfig);
				config.addRouterConfig(routerConfig);
			}

		} catch (Exception e) {
			if (e instanceof ConfigException) {
				throw (ConfigException) e;
			}
			throw new ConfigException("read and parse config error.", e);
		}
		return config;
	}

	void check(String path) throws Exception {
		if (client.exists(path)) {
			log.info(String.format("checked path : namespace=%s, path=%s",
					namespace, path));
		} else {
			throw new ConfigException(String.format(
					"Non-existed path: namespace=%s, path=%s", namespace, path));
		}
	}

	RouterConfig readRouterConfig(BerainEntry entry) throws Exception {
		String routerName = entry.getKey();
		String routerPath = entry.getPath();

		check(routerPath);
		Map<String, String> kv = splitValue(entry.getValue());

		String equalizerClass = getValue(kv, routerPath, FOREST_EQUALIZER_CLASS);
		String routerClass = getValue(kv, routerPath, FOREST_ROUTER_CLASS);
		String plotterClass = getValue(kv, routerPath, FOREST_PLOTTER_CLASS);
		String overflow = getValue(kv, routerPath, FOREST_OVERFLOW);
		String parentId = getValue(kv, routerPath, FOREST_PARENT_ID);
		String parentPath = getValue(kv, routerPath, FOREST_PARENT_PATH);

		RouterConfig routerConfig = new RouterConfig();
		routerConfig.path = routerPath;
		routerConfig.id = routerName;
		routerConfig.parentId = parentId;
		routerConfig.parentPath = parentPath;
		routerConfig.equalizerClass = equalizerClass;
		routerConfig.plotterClass = plotterClass;
		routerConfig.overflow = overflow;
		routerConfig.routerClass = routerClass;
		//
		Map<String, String> defaultExtraInfo = readExtraInfos(routerConfig.path
				+ FOREST_EXTRA_INFO_PATH);
		routerConfig.defaultExtraInfo = defaultExtraInfo;
		//
		routerConfig.resources = readResources(routerConfig.path
				+ FOREST_RESOURCES_PATH);
		routerConfig.slices = readSlices(routerConfig.path + FOREST_SLICES_PATH);
		if (parentPath != null && !"".equals(parentPath)) {
			check(parentPath);
			BerainEntry parentEntry = client.getFull(parentPath);

			RouterConfig config = readRouterConfig(parentEntry);
			routerConfig.parentRouterConfig = config;
		}
		return routerConfig;
	}

	public static String getKey(String path) {
		String[] ps = path.split("/");
		return ps[ps.length - 1];
	}

	public String getValue(Map<String, String> kv, String routerPath,
			String path) throws Exception {
		String key = getKey(path);
		log.debug(String.format("read path: %s", routerPath + path));
		String value = null;
		if (client.exists(routerPath + path)) {
			value = client.get(routerPath + path);
			if (value == null || "".equals(value)
					|| "null".equalsIgnoreCase(value)) {
				return kv.get(key);
			}
		} else {
			log.info("ignored: non-existed path: " + routerPath + path);
		}

		return "null".equalsIgnoreCase(value) ? null : value;
	}

	/**
	 * <pre>
	 *  -router
	 *  	- resources
	 *  		- name=weight/queryString (192.168.1.11:9080:db1=1)/(192.168.1.11:9080:db1=wight=1&user=root&password=pwd)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 *  		- name=weight/queryString (192.168.1.11:9080:db2=1)/(192.168.1.11:9080:db1=wight=1&user=root&password=pwd)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 *  		- name=weight/queryString (192.168.1.12:9080:db1=1)/(192.168.1.11:9080:db1=wight=1&user=root&password=pwd)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 *  		- name=weight/queryString (192.168.1.13:9080:db1=1)/(192.168.1.11:9080:db1=wight=1&user=root&password=pwd)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 * 
	 * 
	 * </pre>
	 * 
	 * @throws Exception
	 */
	Set<ResConfig> readResources(String path) throws Exception {

		check(path);
		Set<ResConfig> resConfigs = new HashSet<>();
		//
		log.info(String.format("read path: namespace=%s, path=%s", namespace,
				path));
		List<String> reses = client.getCuratorFramework().getChildren()
				.forPath(path);

		for (String resPath : reses) {
			String tmpPath = path + "/" + resPath;

			BerainEntry res = client.getFull(tmpPath);
			String name = res.getKey();

			ResConfig resource = new ResConfig();
			resource.name = name;
			List<BerainEntry> entries = client.nextChildren(tmpPath);
			Map<String, String> extraInfo = new HashMap<>();
			Map<String, String> kv = toMap(entries);

			// order 1. put name/value split info
			extraInfo.putAll(splitName(res.getKey()));
			extraInfo.putAll(splitValue(res.getValue()));
			// order 2. put key/value info
			extraInfo.putAll(kv);
			resource.extraInfo = extraInfo;

			String wt = extraInfo.get(KEY_WEIGHT);
			if (wt != null && !"".equals(wt)) {
				resource.weight = Integer.parseInt(wt);
			}

			resConfigs.add(resource);
		}
		return resConfigs;
	}

	/**
	 * <pre>
	 *  -router
	 *  	- slices
	 *  		- id=queryString/emptyString (1:id=1&alias=_1&ReadWrite=192.168.1.11:9080:db1,192.168.1.12:9080:db1,192.168.1.11:9080:db2)/(1:""|null)
	 *  			-alias=_1
	 * 				-read=192.168.1.11:9080:db1,192.168.1.12:9080:db1
	 *  			-write=192.168.1.11:9080:db2/ReadWrite=192.168.1.11:9080:db1,192.168.1.12:9080:db1,192.168.1.11:9080:db2
	 *  			-range=1~100,1000~1024
	 *  		- id=queryString/emptyString (1:id=1&alias=_1&ReadWrite=192.168.1.11:9080:db1,192.168.1.12:9080:db1,192.168.1.11:9080:db2)/(1:""|null)
	 *  			-alias=_1
	 * 				-read=192.168.1.11:9080:db1,192.168.1.12:9080:db1
	 *  			-write=192.168.1.11:9080:db2/ReadWrite=192.168.1.11:9080:db1,192.168.1.12:9080:db1,192.168.1.11:9080:db2
	 *  			-range=1~100,1000~1024
	 * 
	 * 
	 * </pre>
	 * 
	 * @param slicePath
	 * @throws Exception
	 */
	Set<SliceConfig> readSlices(String slicePath) throws Exception {
		check(slicePath);
		Set<SliceConfig> sliceConfigs = new HashSet<>();
		log.info(String.format("read path: namespace=%s, path=%s", namespace,
				slicePath));
		List<String> slicePaths = client.getCuratorFramework().getChildren()
				.forPath(slicePath);

		for (String path : slicePaths) {
			String tmpPath = slicePath + "/" + path;
			SliceConfig sliceConfig = readSliceConfig(tmpPath);
			sliceConfigs.add(sliceConfig);
		}
		return sliceConfigs;
	}

	SliceConfig readSliceConfig(String path) throws Exception {
		BerainEntry entry = client.getFull(path);

		Map<String, String> kv = splitValue(entry.getValue());

		List<BerainEntry> children = client.nextChildren(path);
		long sliceId = getSliceId(entry, children);
		Map<String, String> map = toMap(children);
		kv.putAll(map);

		String alias = MapUtils.getString(kv, S_ALIAS, String.valueOf(sliceId));
		String read = kv.get(S_READ);
		String write = kv.get(S_WRITE);
		String rw = kv.get(S_READ_WRITE);
		String sourceKey = kv.get(S_RANGE);
		String subRouterId = kv.get(S_SUB_ROUTER_ID);
		String subRouterPath = kv.get(S_SUB_ROUTER_PATH);
		Range[] ranges = toRanges(sourceKey);
		SliceConfig sliceConfig = new SliceConfig();
		sliceConfig.path = entry.getPath();
		sliceConfig.id = sliceId;
		sliceConfig.alias = alias;
		sliceConfig.readRes = read;
		sliceConfig.writeRes = write;
		sliceConfig.readWriteRes = rw;

		sliceConfig.sourceKey = sourceKey;
		sliceConfig.ranges = ranges;
		sliceConfig.extraInfo = kv;
		sliceConfig.subRouterId = subRouterId;
		sliceConfig.subRouterPath = subRouterPath;
		if (subRouterPath != null && !"".equals(subRouterPath)) {
			BerainEntry routerEntry = client.getFull(subRouterPath);
			RouterConfig routerConfig = readRouterConfig(routerEntry);
			sliceConfig.subRouter.add(routerConfig);
		}
		String subSlice = kv.get(S_SUB_SLICES_PATH.substring(1));
		if (subSlice != null && !"".equals(subSlice)) {
			SliceConfig subSliceConfig = readSliceConfig(path
					+ S_SUB_SLICES_PATH);
			sliceConfig.subSlices.add(subSliceConfig);
		}
		return sliceConfig;

	}

	<Key> void mapResource(Function function, String res, Router<Key> router,
			long sliceId, String alias, Range[] ranges) {
		if (res != null && !"".equals(res)) {
			String reses[] = toResources(res);
			for (String resourceName : reses) {
				if (ranges == null) {
					router.map(sliceId, alias, resourceName, function);
				} else {
					router.map(resourceName, alias, function, ranges);
				}
			}

		}
	}

	String[] toResources(String res) {
		String[] pairs = res.split(SPLIT_REG);
		return pairs;
	}

	Range[] toRanges(String str) {
		if (str == null && "".equals(str)) {
			return null;
		}
		List<Range> ranges = new ArrayList<>();
		String[] pairs = str.split(SPLIT_REG);
		for (String pair : pairs) {
			if (pair != null && !"".equals(pair.trim())) {
				String[] sks = pair.trim().split(SPLIT_REG_BOUND);
				if (sks.length >= 2) {
					long start = Long.parseLong(sks[0]);
					long end = Long.parseLong(sks[1]);
					Range range = new Range(start, end);
					ranges.add(range);
				} else {
					long start = Long.parseLong(sks[0]);
					long end = start;
					Range range = new Range(start, end);
					ranges.add(range);
				}
			}
		}
		Range[] array = new Range[ranges.size()];

		return ranges.toArray(array);
	}

	long getSliceId(BerainEntry sliceEntry, List<BerainEntry> children) {
		return Long.parseLong(sliceEntry.getKey());
	}

	Map<String, String> splitName(String name) {
		Map<String, String> map = new HashMap<>();

		if (name.contains(":")) {
			String[] ns = name.split(":");
			if (ns.length > 1) {
				map.put(KEY_HOST, ns[0].trim());
				map.put(KEY_PORT, ns[1].trim());
			}
			if (ns.length > 2) {
				map.put(KEY_SCHEMA, ns[2].trim());
			}
		}

		return map;

	}

	Map<String, String> splitValue(String value) {
		Map<String, String> map = new HashMap<>();

		if (value == null) {
			return map;
		}
		// if (value.contains("&")) {
		if (matches(value, SPLIT_REG)) {

			String[] vs = value.split(SPLIT_REG);
			if (vs.length > 0) {
				for (int i = 0; i < vs.length; i++) {
					String[] kv = vs[i].split("=");
					if (kv.length >= 2) {
						map.put(kv[0].toLowerCase().trim(), kv[1].trim());
					}
				}
			}
		} else {
			map.put(KEY_WEIGHT, value);
		}
		return map;

	}

	boolean matches(String str, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		boolean result = m.find();
		return result;
	}

	Map<String, String> toMap(List<BerainEntry> entries) {
		Map<String, String> map = new HashMap<>();
		for (BerainEntry entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	Map<String, String> readExtraInfos(String path) throws Exception {
		List<BerainEntry> entries = client.nextChildren(path
				+ FOREST_EXTRA_INFO_PATH);
		return toMap(entries);
	}
}
