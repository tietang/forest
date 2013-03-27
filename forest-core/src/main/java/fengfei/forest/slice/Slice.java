package fengfei.forest.slice;

import java.util.Map;

import fengfei.forest.slice.Resource.Function;

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

	void setExtraInfo(Map<String, String> extraInfo);

	void addExtraInfo(String key, String value);

	Map<String, String> getExtraInfo();

	void addExtraInfo(Map<String, String> extraInfo);

	void add(Resource resource);

	void add(Resource resource, Function function);

	void remove(Resource resource);

	Resource get(long seed, Function function);

	Resource getAny(long seed);

	Long getSliceId();

	void setSliceId(Long sliceId);
}