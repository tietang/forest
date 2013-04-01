package fengfei.forest.slice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.SliceResource.Function;

public class Config {

	private List<RouterConfig> routers = new ArrayList<>();
	private Map<String, RouterConfig> routerMap = new HashMap<>();
	public Map<String, String> defaultExtraInfo = new HashMap<>();

	public List<RouterConfig> getRouters() {
		return routers;
	}

	public void setRouters(List<RouterConfig> routers) {
		this.routers = routers;
	}

	public void addRouterConfig(RouterConfig routerConfig) {
		this.routers.add(routerConfig);
	}

	public Map<String, RouterConfig> getRouterMap() {
		if (routerMap == null) {
			routerMap = new HashMap<>();
		}
		for (RouterConfig router : routers) {
			routerMap.put(router.id, router);
		}
		return routerMap;
	}

	@Override
	public String toString() {
		return "Config [ routerMap=" + routerMap + "]";
	}

	public static class RouterConfig {

		public String id;
		public String parentId;
		public String equalizerClass;
		public String routerClass;
		public String plotterClass;
		public String overflow = OverflowType.Last.name();
		public String selectType = SelectType.Loop.name();
		public Map<String, String> defaultExtraInfo = new HashMap<>();
		public Set<SliceConfig> slices = new HashSet<>();

		protected RouterConfig copy() {
			RouterConfig config = new RouterConfig();
			config.id = id;
			config.parentId = id;
			config.defaultExtraInfo = defaultExtraInfo;
			config.equalizerClass = equalizerClass;
			config.plotterClass = plotterClass;
			config.overflow = overflow;
			config.routerClass = routerClass;
			config.selectType = selectType;
			config.slices = slices;
			return config;
		}

		public void addDefaultExtraInfo(Map<String, String> defaultExtraInfo) {
			this.defaultExtraInfo.putAll(defaultExtraInfo);
		}

		public List<SliceConfig> getSliceList() {
			return new ArrayList<>(slices);
		}
	}

	public static class SliceConfig {

		public Long id;
		public String sourceKey;
		public int weight = 1;
		public Map<String, String> extraInfo;
		public RouterConfig subRouterConfig;
		public List<ResConfig> resConfigs = new ArrayList<>();
		public List<SliceConfig> subSlices = new ArrayList<>();
	}

	public static class ResConfig {

		public String name;
		public int weight = 0;
		public String function = Function.ReadWrite.name();
		public Map<String, String> extraInfo = new HashMap<>();
	}
}
