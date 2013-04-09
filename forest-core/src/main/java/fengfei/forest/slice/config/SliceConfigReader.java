package fengfei.forest.slice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.Config.ResConfig;
import fengfei.forest.slice.config.Config.RouterConfig;
import fengfei.forest.slice.config.Config.SliceConfig;
import fengfei.forest.slice.config.zk.MapUtils;
import fengfei.forest.slice.exception.ConfigException;

public class SliceConfigReader implements SliceReader<Config> {

	static Logger log = LoggerFactory.getLogger(SliceConfigReader.class);
	//
	public static final String FOREST_ROOT_PATH = "/routers";
	//
	public static final String FOREST_ROUTER_CLASS = "/routerClass";
	public static final String FOREST_EQUALIZER_CLASS = "/eqClass";
	public static final String FOREST_PLOTTER_CLASS = "/plotterClass";
	public static final String FOREST_PARENT_ID = "/parentId";
	public static final String FOREST_PARENT_PATH = "/parentPath";
	public static final String FOREST_OVERFLOW = "/overflow";

	public static final String FOREST_RESOURCES_PATH = "/resources";
	public static final String FOREST_EXTRA_INFO_PATH = "/extraInfos";
	public static final String FOREST_SLICES_PATH = "/slices";
	public static final String S_SUB_SLICES_PATH = "/subSlices";

	//
	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_SCHEMA = "schema";
	//
	public static final String S_ALIAS = "alias";
	public static final String S_FUNCTION = "func";
	public static final String S_RESOURCE_NAME = "resName";
	public static final String S_READ = "read";
	public static final String S_WRITE = "write";
	public static final String S_READ_WRITE = "readwrite";
	public static final String S_RANGE = "range";
	public static final String S_SUB_ROUTER_ID = "subId";
	public static final String S_SUB_ROUTER_PATH = "subPath";
	//
	public static final String SPLIT_REG = "&|,|\\||，|	|\n|\r|\n\r";
	public static final String SPLIT_REG_BOUND = "~|-|－|——|—";
	protected ConfigSource source;
	protected String namespace = "";

	public SliceConfigReader() {
	}

	public SliceConfigReader(ConfigSource source) {
		this.source = source;
	}

	public void setSource(ConfigSource source) {
		this.source = source;
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
		if (null == rootPath || "".equals(rootPath) || "/".equals(rootPath)
				|| rootPath.length() == 1) {
			throw new ConfigException(String.format(
					"root path is invalid: namespace=%s, path=%s", namespace,
					rootPath));

		}
		Config config = new Config();

		config.setPath(rootPath + FOREST_ROOT_PATH);

		try {
			check(config.path);
			List<BerainEntry> routerEntries = source.children(config.path);
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
		if (source.exists(path)) {
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
			BerainEntry parentEntry = source.getFull(parentPath);

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
		if (source.exists(routerPath + path)) {
			value = source.get(routerPath + path);
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
	public Set<ResConfig> readResources(String path) throws Exception {

		check(path);
		Set<ResConfig> resConfigs = new HashSet<>();
		//
		log.info(String.format("read path: namespace=%s, path=%s", namespace,
				path));
		List<String> reses = source.listChildren(path);

		for (String resPath : reses) {
			String tmpPath = path + "/" + resPath;

			BerainEntry res = source.getFull(tmpPath);
			String name = res.getKey();

			ResConfig resource = new ResConfig();
			resource.name = name;
			List<BerainEntry> entries = source.children(tmpPath);
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
	public Set<SliceConfig> readSlices(String slicePath) throws Exception {
		check(slicePath);
		Set<SliceConfig> sliceConfigs = new HashSet<>();
		log.info(String.format("read path: namespace=%s, path=%s", namespace,
				slicePath));
		List<String> slicePaths = source.listChildren(slicePath);

		for (String path : slicePaths) {
			String tmpPath = slicePath + "/" + path;
			SliceConfig sliceConfig = readSliceConfig(tmpPath);
			sliceConfigs.add(sliceConfig);
		}
		return sliceConfigs;
	}

	public SliceConfig readSliceConfig(String path) throws Exception {
		BerainEntry entry = source.getFull(path);

		Map<String, String> kv = splitValue(entry.getValue());

		List<BerainEntry> children = source.children(path);

		Map<String, String> map = toMap(children);
		long sliceId = getSliceId(entry, map);
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
			BerainEntry routerEntry = source.getFull(subRouterPath);
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

	public <Key> void mapResource(Function function, String res,
			Router<Key> router, long sliceId, String alias, Range[] ranges) {
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

	protected String[] toResources(String res) {
		String[] pairs = res.split(SPLIT_REG);
		return pairs;
	}

	protected Range[] toRanges(String str) {
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

	protected long getSliceId(BerainEntry sliceEntry,
			Map<String, String> children) {
		return Long.parseLong(sliceEntry.getKey());
	}

	protected Map<String, String> splitName(String name) {
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

	protected Map<String, String> splitValue(String value) {
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

	protected boolean matches(String str, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		boolean result = m.find();
		return result;
	}

	protected Map<String, String> toMap(List<BerainEntry> entries) {
		Map<String, String> map = new HashMap<>();
		for (BerainEntry entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	protected Map<String, String> readExtraInfos(String path) throws Exception {
		String info = source.get(path + FOREST_EXTRA_INFO_PATH);
		Map<String, String> kv = splitValue(info);
		List<BerainEntry> entries = source.children(path
				+ FOREST_EXTRA_INFO_PATH);
		Map<String, String> map = toMap(entries);
		kv.putAll(map);
		return kv;
	}
}
