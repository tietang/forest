package fengfei.forest.slice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.config.Config.ResConfig;
import fengfei.forest.slice.config.Config.RouterConfig;
import fengfei.forest.slice.config.Config.SliceConfig;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.impl.ReadWriteSlice;

public class DefaultRouterFactory implements RouterFactory {

	protected Map<String, Router<?>> routers = new HashMap<>();
	protected Map<String, RouterConfig> routerConfigCache = new HashMap<>();

	public DefaultRouterFactory(Config config) {
		config(config);
	}

	public void config(Config config) {
		List<RouterConfig> rcs = config.getRouters();
		for (RouterConfig rc : rcs) {
			routerConfigCache.put(rc.id, rc);
		}
		for (RouterConfig rc : rcs) {
			try {
				RouterConfig routerConfig = inherit(rc);
				routerConfigCache.put(rc.id, routerConfig);
			} catch (CloneNotSupportedException e) {

				e.printStackTrace();
			}
		}

	}

	private RouterConfig inherit(RouterConfig routerConfig)
			throws CloneNotSupportedException {
		String parentId = routerConfig.parentId;
		if (parentId == null) {
			return routerConfig;
		} else {
			RouterConfig parentGroup = routerConfigCache.get(parentId);
			if (parentGroup == null) {
				return routerConfig;
			} else {
				RouterConfig config = inherit(parentGroup);
				// FIXME copy
				config = config.copy();
				config.id = routerConfig.id;
				config.selectType = routerConfig.selectType == null ? config.selectType
						: routerConfig.selectType;
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
				config.slices = routerConfig.slices;

				return config;

			}

		}

	}

	<Key> Router<Key> create(RouterConfig routerConfig) {
		//
		Router<Key> router = newInstance(routerConfig.routerClass);
		//
		Equalizer<Key> equalizer = null;
		String equalizerClass = routerConfig.equalizerClass;
		if (equalizerClass != null && !"".equals(equalizerClass)) {
			equalizer = newInstance(equalizerClass);
		}

		router.setOverflowType(OverflowType.find(routerConfig.overflow));
		router.setEqualizer(equalizer);

		build(routerConfig, router);
		return router;

	}

	private <Key> void build(RouterConfig routerConfig, Router<Key> router) {

		List<SliceConfig> sliceConfigs = routerConfig.getSliceList();

		int size = sliceConfigs.size();
		for (int i = 0; i < size; i++) {
			SliceConfig sliceConfig = sliceConfigs.get(i);
			if (sliceConfig.id == null || sliceConfig.id < 0) {
				sliceConfig.id = Long.valueOf(i);
			}
			Long sliceId = sliceConfig.id;
			Slice<Key> slice = create(sliceId, sliceConfig, routerConfig);
			Range[] ranges = splitSourceKey(sliceConfig.sourceKey);
			router.register(slice, ranges);

		}

	}

	private Range[] splitSourceKey(String sourceKey) {
		List<Range> longs = new ArrayList<>();

		String[] sources = sourceKey.split(",| ");
		for (String sk : sources) {
			if (sk != null && !"".equals(sk.trim())) {
				String[] sks = sk.split("~|-|－|——|—|--");
				if (sks.length >= 2) {
					long start = Long.parseLong(sks[0]);
					long end = Long.parseLong(sks[1]);
					Range range = new Range(start, end);
					longs.add(range);
				} else {
					long start = Long.parseLong(sks[0]);
					long end = Long.parseLong(sks[0]);
					Range range = new Range(start, end);
					longs.add(range);
				}
			}
		}

		return longs.toArray(new Range[longs.size()]);

	}

	public <Key> Slice<Key> create(Long sliceId, SliceConfig sliceConfig,
			RouterConfig routerConfig) {
		Map<String, String> defaultExtraInfo = routerConfig.defaultExtraInfo;
		Slice<Key> slice = new ReadWriteSlice<>();
		Plotter plotter = newInstance(routerConfig.plotterClass);
		slice.setPlotter(plotter);

		slice.addParams(defaultExtraInfo);
		slice.addParams(sliceConfig.extraInfo);
		slice.setSliceId(sliceId);

		buildReource(sliceConfig.resConfigs, slice);
		//
		List<SliceConfig> subSliceConfigs = sliceConfig.subSlices;
		if (subSliceConfigs != null && subSliceConfigs.size() > 0) {
			RouterConfig subRouterConfig = routerConfig.copy();
			subRouterConfig.slices = new HashSet<>(sliceConfig.subSlices);
			subRouterConfig.parentId = routerConfig.id;
			Router<Key> childRouter = create(subRouterConfig);
			slice.setChildRouter(childRouter);
		}

		return slice;
	}

	private <Key> void buildReource(List<ResConfig> resConfigs, Slice<Key> slice) {
		int size = resConfigs.size();
		for (int i = 0; i < size; i++) {
			ResConfig resConfig = resConfigs.get(i);
			SliceResource resource = create(resConfig);
			resource.setSliceId(slice.getSliceId());
			slice.add(resource);
		}

	}

	public SliceResource create(ResConfig resConfig) {

		//
		SliceResource resource = new SliceResource(resConfig.name, resConfig.schema);
		resource.addExtraInfo(resConfig.extraInfo);
		resource.addExtraInfo(resource.getExtraInfo());
		resource.setWeight(resConfig.weight);
		resource.setFunction(Function.find(resConfig.function));

		return resource;
	}

	@SuppressWarnings("unchecked")
	private <T> T newInstance(String className) {
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
			RouterConfig routerConfig = routerConfigCache.get(routerName);
			System.out.println("1:  " + routerConfig);
			router = create(routerConfig);
			routers.put(routerConfig.id, router);
		}
		if (router == null) {
			throw new NonExistedSliceException("unitName=" + routerName);
		}
		return router;

	}

	public <Source> Router<Source> getRouter(Equalizer<Source> equalizer,
			String routerName) {
		Router<Source> router = getRouter(routerName);
		if (equalizer != null) {
			router.setEqualizer(equalizer);
		}

		return router;

	}

}
