package fengfei.forest.slice;

import java.util.Map;

import fengfei.forest.slice.SliceResource.Function;

/**
 * 一个slice是由一系列相同resource组成
 * 
 * 
 * 
 * @param <Key>
 */
public interface Slice<Key> {

	Router<Key> getChildRouter();

	void setChildRouter(Router<Key> childRouter);

	void setPlotter(Plotter plotter);

	void setParams(Map<String, String> params);

	void addParams(String key, String value);

	Map<String, String> getParams();

	void addParams(Map<String, String> params);

	void add(SliceResource resource);

	void add(SliceResource resource, Function function);

	void remove(SliceResource resource);

	SliceResource get(long seed, Function function);

	SliceResource getAny(long seed);

	Long getSliceId();

	void setSliceId(Long sliceId);

	String getAlias();

	void setAlias(String alias);
}