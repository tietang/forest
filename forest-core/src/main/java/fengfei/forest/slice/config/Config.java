package fengfei.forest.slice.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.SliceResource.Function;

public class Config {
	public String path;
	private List<RouterConfig> routers = new ArrayList<>();
	private Map<String, RouterConfig> routerMap = new HashMap<>();
	public Map<String, String> defaultExtraInfo = new HashMap<>();

	public Config() {
	}

	public List<RouterConfig> getRouters() {
		return routers;
	}

	public void setPath(String path) {
		this.path = path;
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
		public String path;

		public String id;
		public String parentId;
		public String parentPath;
		public RouterConfig parentRouterConfig;
		public String equalizerClass;
		public String routerClass;
		public String plotterClass;
		public String overflow = OverflowType.Last.name();
		public Map<String, String> defaultExtraInfo = new HashMap<>();
		public Set<ResConfig> resources = new HashSet<>();
		public Set<SliceConfig> slices = new HashSet<>();

		protected RouterConfig copy() {
			RouterConfig config = new RouterConfig();
			config.id = id;
			config.parentId = parentId;
			config.parentPath = parentPath;
			config.parentRouterConfig = parentRouterConfig;
			config.defaultExtraInfo = defaultExtraInfo;
			config.equalizerClass = equalizerClass;
			config.plotterClass = plotterClass;
			config.overflow = overflow;
			config.routerClass = routerClass;
			config.slices = new HashSet<>(slices);
			config.resources = new HashSet<>(resources);
			return config;
		}

		public void addDefaultExtraInfo(Map<String, String> defaultExtraInfo) {
			this.defaultExtraInfo.putAll(defaultExtraInfo);
		}

		public List<SliceConfig> getSliceList() {
			return new ArrayList<>(slices);
		}

		@Override
		public String toString() {
			return "RouterConfig [path=" + path + ", id=" + id + ", parentId="
					+ parentId + ", parentPath=" + parentPath
					+ ", equalizerClass=" + equalizerClass + ", routerClass="
					+ routerClass + ", plotterClass=" + plotterClass
					+ ", overflow=" + overflow + ", defaultExtraInfo="
					+ defaultExtraInfo + ", resources=" + resources
					+ ", slices=" + slices + "]";
		}
	}

	public static class SliceConfig {
		public String path;
		public Long id;
		public String alias;
		public String readRes;
		public String writeRes;
		public String readWriteRes;
		public String sourceKey;
		public Range[] ranges;
		public Map<String, String> extraInfo;
		public String subRouterId;
		public String subRouterPath;
		public List<RouterConfig> subRouter = new ArrayList<>();
		public List<ResConfig> resConfigs = new ArrayList<>();
		public List<SliceConfig> subSlices = new ArrayList<>();

		@Override
		public String toString() {
			return "SliceConfig [path=" + path + ", id=" + id + ", alias="
					+ alias + ", readRes=" + readRes + ", writeRes=" + writeRes
					+ ", readWriteRes=" + readWriteRes + ", sourceKey="
					+ sourceKey + ", ranges=" + Arrays.toString(ranges)
					+ ", extraInfo=" + extraInfo + ", subRouterId="
					+ subRouterId + ", subRouterPath=" + subRouterPath
					+ ", subRouter=" + subRouter + ", resConfigs=" + resConfigs
					+ ", subSlices=" + subSlices + "]";
		}
	}

	public static class ResConfig {
		public String path;
		public String name;
		public int weight = 0;
		public String function = Function.ReadWrite.name();
		public Map<String, String> extraInfo = new HashMap<>();

		@Override
		public String toString() {
			return "ResConfig [path=" + path + ", name=" + name + ", weight="
					+ weight + ", function=" + function + ", extraInfo="
					+ extraInfo + "]";
		}
	}
}
