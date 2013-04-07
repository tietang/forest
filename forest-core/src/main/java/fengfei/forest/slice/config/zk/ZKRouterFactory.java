package fengfei.forest.slice.config.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.zk.ZkBerainClient;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.AbstractRouterFactory;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.exception.ConfigException;
import fengfei.forest.slice.utils.ResourcesUtils;

public class ZKRouterFactory extends AbstractRouterFactory {
	static Logger log = LoggerFactory.getLogger(ZKRouterFactory.class);
	//
	static final String FOREST_ROOT_PATH = "/router";
	//
	static final String FOREST_ROUTER_CLASS = "/router_class";
	static final String FOREST_EQUALIZER_CLASS = "/eq_class";
	static final String FOREST_PLOTTER_CLASS = "/plotter_class";

	static final String FOREST_RESOURCES_PATH = "/resources";
	static final String FOREST_EXTRA_INFO_PATH = "/extra_infos";
	static final String FOREST_SLICES_PATH = "/slices";

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

	ZkBerainClient client;
	String forest_path;

	public ZKRouterFactory() {

		try {
			Properties properties = ResourcesUtils
					.getResourceAsProperties("zk.properties");
			client = new ZkBerainClient(properties);
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	@SuppressWarnings("unchecked")
	public <Key> void read() throws Exception {

		try {

			List<BerainEntry> routerEntries = client
					.nextChildren(FOREST_ROOT_PATH);
			for (BerainEntry entry : routerEntries) {
				String routerName = entry.getKey();
				String routerPath = entry.getPath();
				String equalizerClass = client.get(routerPath
						+ FOREST_EQUALIZER_CLASS);
				String routerClass = client.get(routerPath
						+ FOREST_ROUTER_CLASS);
				String plotterClass = client.get(routerPath
						+ FOREST_PLOTTER_CLASS);

				Router<Key> router = (Router<Key>) newInstance(routerClass);

				if (equalizerClass != null && !"".equals(equalizerClass)) {
					Equalizer<Key> equalizer = (Equalizer<Key>) newInstance(equalizerClass);
					router.setEqualizer(equalizer);
				}
				if (plotterClass != null && !"".equals(plotterClass)) {
					Plotter plotter = (Plotter) newInstance(plotterClass);
					router.setPlotter(plotter);
				}
				readResources(routerPath + FOREST_RESOURCES_PATH, router);
				readSlices(routerPath + FOREST_SLICES_PATH, router);
				routers.put(routerName, router);

			}

		} catch (Exception e) {
			throw new ConfigException("read and parse confi error.", e);
		}
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
	<Key> void readResources(String path, Router<Key> router) throws Exception {

		//
		List<String> reses = client.getCuratorFramework().getChildren()
				.forPath(path);
		Map<String, String> extraInfo = readExtraInfos();
		for (String resPath : reses) {
			BerainEntry res = client.getFull(resPath);
			String name = res.getKey();

			Resource resource = new Resource(name);
			List<BerainEntry> entries = client.nextChildren(resPath);
			Map<String, String> kv = toMap(entries);
			// order 1. put top extraInfo
			resource.addExtraInfo(extraInfo);
			// order 2. put name/value split info
			Map<String, String> map = splitNameValue(res);
			resource.addExtraInfo(map);
			String wt = map.get(KEY_WEIGHT);
			if (wt != null && !"".equals(wt)) {
				resource.setWeight(Integer.parseInt(wt));
			}
			// order 3. put key/value info
			resource.addExtraInfo(kv);
			router.register(resource);
		}
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
	 * @param router
	 * @throws Exception
	 */
	<Key> void readSlices(String slicePath, Router<Key> router)
			throws Exception {
		List<String> slicePaths = client.getCuratorFramework().getChildren()
				.forPath(slicePath);

		for (String path : slicePaths) {
			BerainEntry entry = client.getFull(path);

			Map<String, String> kv = splitNameValue(entry);

			List<BerainEntry> children = client.nextChildren(path);
			long sliceId = getSliceId(entry, children);
			Map<String, String> map = toMap(children);
			kv.putAll(map);

			String alias = MapUtils.getString(kv, S_ALIAS,
					String.valueOf(sliceId));
			String read = kv.get(S_READ);
			String write = kv.get(S_WRITE);
			String rw = kv.get(S_READ_WRITE);

			Range[] ranges = toRanges(kv.get(S_RANGE));
			mapResource(Function.Read, read, router, sliceId, alias, ranges);
			mapResource(Function.Write, write, router, sliceId, alias, ranges);
			mapResource(Function.ReadWrite, rw, router, sliceId, alias, ranges);

		}
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

	Map<String, String> readExtraInfos() throws Exception {
		List<BerainEntry> entries = client.nextChildren(FOREST_EXTRA_INFO_PATH);
		return toMap(entries);
	}

	@Override
	public void config(Config config) {

	}

}
