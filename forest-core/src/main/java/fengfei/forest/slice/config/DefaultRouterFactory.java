package fengfei.forest.slice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fengfei.berain.client.BerainEntry;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.Config.ResConfig;
import fengfei.forest.slice.config.Config.RouterConfig;
import fengfei.forest.slice.config.Config.SliceConfig;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;
import fengfei.forest.slice.config.zk.ZKSliceConfigReader;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.impl.ReadWriteSlice;

public class DefaultRouterFactory extends AbstractRouterFactory {

	protected Map<String, Router<?>> routers = new HashMap<>();
	protected Map<String, RouterConfig> routerConfigCache = new HashMap<>();

	public DefaultRouterFactory(Config config) {
		config(config);
	}

	public static void main(String[] args) {
		// SliceConfigReader reader = new ZKSliceConfigReader();
		SliceConfigReader reader = new XmlSliceConfigReader("cp:config.xml");

		Config config = reader.read("/root/main");
		RouterFactory factory = new DefaultRouterFactory(config);
		Router<Long> router = factory.getRouter("r01");
		System.out.println(router.locate(1l));
		System.out.println(router.locate(31423l));
		System.out.println(router.locate(12l));
		router = factory.getRouter("r02");
		System.out.println(router.locate(1l));
		System.out.println(router.locate(31423l));
		System.out.println(router.locate(12l));
		// System.out.println(router);
	}

	public void config(Config config) {
		List<RouterConfig> rcs = config.getRouters();
		for (RouterConfig rc : rcs) {
			routerConfigCache.put(rc.id, rc);
		}
		for (RouterConfig rc : rcs) {
			try {
				RouterConfig routerConfig = inheritByParentRouterConfig(rc);
				routerConfig = inheritByParentId(rc);
				routerConfigCache.put(rc.id, routerConfig);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	private RouterConfig inheritByParentId(RouterConfig routerConfig)
			throws CloneNotSupportedException {

		String parentId = routerConfig.parentId;

		if (parentId == null) {
			return routerConfig;
		} else {
			RouterConfig parentRouterConfig = routerConfigCache.get(parentId);
			if (parentRouterConfig == null) {
				return routerConfig;
			} else {
				RouterConfig config = inheritByParentId(parentRouterConfig);
				// FIXME copy
				config = config.copy();
				config.id = routerConfig.id;
				config.parentId = routerConfig.parentId;
				config.parentPath = routerConfig.parentPath;
				config.routerClass = routerConfig.routerClass == null ? config.routerClass
						: routerConfig.routerClass;
				config.plotterClass = routerConfig.plotterClass == null ? config.plotterClass
						: routerConfig.plotterClass;
				config.overflow = routerConfig.overflow == null ? config.overflow
						: routerConfig.overflow;
				config.equalizerClass = routerConfig.equalizerClass == null ? config.equalizerClass
						: routerConfig.equalizerClass;
				config.addDefaultExtraInfo(new HashMap<>(
						routerConfig.defaultExtraInfo));
				config.resources = new HashSet<>(routerConfig.resources);
				config.slices = new HashSet<>(routerConfig.slices);
				return config;
			}
		}
	}

	private RouterConfig inheritByParentRouterConfig(RouterConfig routerConfig)
			throws CloneNotSupportedException {
		RouterConfig parentRouterConfig = routerConfig.parentRouterConfig;

		if (parentRouterConfig == null) {
			return routerConfig;
		} else {
			if (parentRouterConfig.parentRouterConfig != null
					&& !"".equals(parentRouterConfig.parentRouterConfig)) {
				inheritByParentRouterConfig(parentRouterConfig);
			}
			RouterConfig config = parentRouterConfig.copy();
			// FIXME copy
			config = config.copy();
			config.id = routerConfig.id;
			config.parentId = routerConfig.parentId;
			config.parentPath = routerConfig.parentPath;
			config.routerClass = routerConfig.routerClass == null ? config.routerClass
					: routerConfig.routerClass;
			config.plotterClass = routerConfig.plotterClass == null ? config.plotterClass
					: routerConfig.plotterClass;
			config.overflow = routerConfig.overflow == null ? config.overflow
					: routerConfig.overflow;
			config.equalizerClass = routerConfig.equalizerClass == null ? config.equalizerClass
					: routerConfig.equalizerClass;
			config.addDefaultExtraInfo(new HashMap<>(
					routerConfig.defaultExtraInfo));
			config.resources = new HashSet<>(routerConfig.resources);
			config.slices = new HashSet<>(routerConfig.slices);
			return config;

		}
	}

	<Key> Router<Key> create(RouterConfig routerConfig) throws Exception {
		//
		String equalizerClass = routerConfig.equalizerClass;
		String routerClass = routerConfig.routerClass;
		String plotterClass = routerConfig.plotterClass;
		Router<Key> router = newInstance(routerClass);
		if (equalizerClass != null && !"".equals(equalizerClass)) {
			Equalizer<Key> equalizer = newInstance(equalizerClass);
			router.setEqualizer(equalizer);
		}
		if (plotterClass != null && !"".equals(plotterClass)) {
			Plotter plotter = newInstance(plotterClass);
			router.setPlotter(plotter);
		}
		router.setOverflowType(OverflowType.find(routerConfig.overflow));
		buildResources(routerConfig, router);
		buildSlices(routerConfig, router);
		return router;
	}

	<Key> void buildResources(RouterConfig routerConfig, Router<Key> router)
			throws Exception {
		Set<ResConfig> reses = routerConfig.resources;
		Map<String, String> extraInfo = routerConfig.defaultExtraInfo;
		for (ResConfig res : reses) {
			String name = res.name;
			Resource resource = new Resource(name);
			// order 1. put router extraInfo
			resource.addExtraInfo(extraInfo);
			// order 2. put key/value info
			resource.addExtraInfo(res.extraInfo);
			resource.setWeight(res.weight);
			router.register(resource);
		}
	}

	private <Key> void buildSlices(RouterConfig routerConfig, Router<Key> router)
			throws Exception {
		List<SliceConfig> sliceConfigs = routerConfig.getSliceList();
		int size = sliceConfigs.size();
		for (int i = 0; i < size; i++) {
			SliceConfig sliceConfig = sliceConfigs.get(i);
			long sliceId = sliceConfig.id;
			String alias = sliceConfig.alias;
			Slice<Key> slice = create(sliceId, sliceConfig, routerConfig);
			router.register(slice);

			String read = sliceConfig.readRes;
			String write = sliceConfig.writeRes;
			String rw = sliceConfig.readWriteRes;
			Range[] ranges = sliceConfig.ranges;
			if (ranges == null || ranges.length == 0) {
				router.register(slice);
			} else {
				router.register(slice, ranges);
			}
			mapResource(Function.Read, read, router, sliceId, alias, ranges);
			mapResource(Function.Write, write, router, sliceId, alias, ranges);
			mapResource(Function.ReadWrite, rw, router, sliceId, alias, ranges);

		}
	}

	public <Key> Slice<Key> create(Long sliceId, SliceConfig sliceConfig,
			RouterConfig routerConfig) throws Exception {
		Map<String, String> defaultExtraInfo = routerConfig.defaultExtraInfo;
		Slice<Key> slice = new ReadWriteSlice<>(sliceId);

		Plotter plotter = newInstance(routerConfig.plotterClass);
		slice.setPlotter(plotter);
		slice.addParams(defaultExtraInfo);
		slice.addParams(sliceConfig.extraInfo);
		buildReource(routerConfig, sliceConfig, sliceConfig.resConfigs, slice);

		//
		List<SliceConfig> subSliceConfigs = sliceConfig.subSlices;
		if (subSliceConfigs != null && subSliceConfigs.size() > 0) {
			RouterConfig subRouterConfig = routerConfig.copy();
			subRouterConfig.slices = new HashSet<>(sliceConfig.subSlices);
			// subRouterConfig.parentId = routerConfig.id;
			Router<Key> childRouter = create(subRouterConfig);
			slice.setChildRouter(childRouter);
		}

		return slice;
	}

	private <Key> void buildReource(RouterConfig routerConfig,
			SliceConfig sliceConfig, List<ResConfig> resConfigs,
			Slice<Key> slice) {
		if (resConfigs == null || resConfigs.isEmpty()) {
			return;
		}
		int size = resConfigs.size();
		for (int i = 0; i < size; i++) {
			ResConfig resConfig = resConfigs.get(i);
			SliceResource sliceResource = create(routerConfig, sliceConfig,
					resConfig);
			sliceResource.setSliceId(slice.getSliceId());
			slice.add(sliceResource);
		}
	}

	public SliceResource create(RouterConfig routerConfig,
			SliceConfig sliceConfig, ResConfig resConfig) {
		//
		Resource resource = new Resource(resConfig.name);
		// order 1. put router extraInfo
		resource.addExtraInfo(resConfig.extraInfo);
		// order 2. put slice extraInfo
		resource.addExtraInfo(sliceConfig.extraInfo);
		// order 3. put key/value info
		resource.addExtraInfo(resConfig.extraInfo);
		resource.setWeight(resConfig.weight);
		SliceResource sliceResource = new SliceResource(resource);
		sliceResource.setAlias(sliceConfig.alias);
		sliceResource.setFunction(Function.find(resConfig.function));
		return sliceResource;
	}

	@SuppressWarnings("unchecked")
	protected <T> T newInstance(String className) {
		try {
			return (T) Class.forName(className.trim()).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			throw new IllegalArgumentException("non-exist class: " + className);
		}
	}

	public <Key> Router<Key> getRouter(String routerName) {
		@SuppressWarnings("unchecked")
		Router<Key> router = (Router<Key>) routers.get(routerName);
		if (router == null) {
			try {
				RouterConfig routerConfig = routerConfigCache.get(routerName);
				// //System.out.println("1:  " + routerConfig);
				router = create(routerConfig);
				routers.put(routerConfig.id, router);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (router == null) {
			throw new NonExistedSliceException("unitName=" + routerName);
		}
		return router;
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
}
