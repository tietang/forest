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

	@Override
	public Config read(String path) throws ConfigException {

		return null;
	}

	static Logger log = LoggerFactory.getLogger(ZKRouterFactory.class);
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

	ZkBerainClient client;
	String rootPath;

	public SliceConfigReader() {

		try {
			Properties properties = ResourcesUtils
					.getResourceAsProperties("zk.properties");
			client = new ZkBerainClient(properties);
			rootPath = properties.getProperty("rootPath");
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	public SliceConfigReader(String zkProperties) {

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(zkProperties));
			client = new ZkBerainClient(properties);
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * <pre>
	 * -root_path - routers - router_parent01 - r01 - r02 - root_path - routers
	 * 		- router_parent01 - r01 - r02
	 * 
	 * 
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public <Key> void read() throws Exception {
		Config config = new Config();

		config.setPath(rootPath + FOREST_ROOT_PATH);
		try {
			List<BerainEntry> routerEntries = client.nextChildren(config.path);
			for (BerainEntry entry : routerEntries) {
				RouterConfig routerConfig = readRouterConfig(entry);
				config.addRouterConfig(routerConfig);
			}

		} catch (Exception e) {
			throw new ConfigException("read and parse confi error.", e);
		}
	}

	RouterConfig readRouterConfig(BerainEntry entry) throws Exception {
		String routerName = entry.getKey();
		String routerPath = entry.getPath();
		String equalizerClass = client.get(routerPath + FOREST_EQUALIZER_CLASS);
		String routerClass = client.get(routerPath + FOREST_ROUTER_CLASS);
		String plotterClass = client.get(routerPath + FOREST_PLOTTER_CLASS);
		String overflow = client.get(routerPath + FOREST_OVERFLOW);
		String parentId = client.get(routerPath + FOREST_PARENT_ID);
		String parentPath = client.get(routerPath + FOREST_PARENT_PATH);

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
			BerainEntry parentEntry = client.getFull(parentPath);
			RouterConfig config = readRouterConfig(parentEntry);
			routerConfig.parentRouterConfig = config;
		}
		return routerConfig;
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
		Set<ResConfig> resConfigs = new HashSet<>();
		//
		List<String> reses = client.getCuratorFramework().getChildren()
				.forPath(path);
		for (String resPath : reses) {
			BerainEntry res = client.getFull(resPath);
			String name = res.getKey();

			ResConfig resource = new ResConfig();
			resource.name = name;
			List<BerainEntry> entries = client.nextChildren(resPath);
			Map<String, String> extraInfo = new HashMap<>();
			Map<String, String> kv = toMap(entries);

			// order 1. put name/value split info
			Map<String, String> map = splitNameValue(res);
			extraInfo.putAll(map);
			// order 2. put key/value info
			extraInfo.putAll(kv);
			resource.extraInfo = extraInfo;

			String wt = map.get(KEY_WEIGHT);
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
		Set<SliceConfig> sliceConfigs = new HashSet<>();
		List<String> slicePaths = client.getCuratorFramework().getChildren()
				.forPath(slicePath);

		for (String path : slicePaths) {
			SliceConfig sliceConfig = readSliceConfig(path);
			sliceConfigs.add(sliceConfig);
		}
		return sliceConfigs;
	}

	SliceConfig readSliceConfig(String path) throws Exception {
		BerainEntry entry = client.getFull(path);

		Map<String, String> kv = splitNameValue(entry);

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
		String[] pairs = res.split(",|\\||，|	|\n|\r|\n\r");
		return pairs;
	}

	Range[] toRanges(String str) {
		if (str == null && "".equals(str)) {
			return null;
		}
		List<Range> ranges = new ArrayList<>();
		String[] pairs = str.split(",|\\||，|	|\n|\r|\n\r");
		for (String pair : pairs) {
			if (pair != null && !"".equals(pair.trim())) {
				String[] sks = pair.trim().split("~|-|－|——|—");
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

	Map<String, String> splitNameValue(BerainEntry entry) {
		Map<String, String> map = new HashMap<>();
		String name = entry.getKey();
		if (name.contains(":")) {
			String[] ns = name.split(":");
			if (ns.length > 1) {
				map.put(KEY_HOST, ns[0]);
				map.put(KEY_PORT, ns[1]);
			}
			if (ns.length > 2) {
				map.put(KEY_SCHEMA, ns[2]);
			}
		}

		String value = entry.getValue();
		if (value == null) {
			return map;
		}
		if (value.contains("&")) {
			String[] vs = value.split("&");
			if (vs.length > 0) {
				for (int i = 0; i < vs.length; i++) {
					String[] kv = vs[i].split("=");
					if (kv.length >= 2) {
						map.put(kv[0], kv[1]);
					}
				}
			}
		} else {

			map.put(KEY_WEIGHT, value);
		}
		return map;

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
