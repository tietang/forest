package fengfei.forest.slice;

import java.util.List;
import java.util.Map;

import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.ResourceTribe;

/**
 * 一个slice是由一系列相同resource组成
 * 
 * 
 * 
 * @param <Key>
 */
public interface Slice<Key> extends Failover {

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

	List<SliceResource> getResources();

	ResourceTribe getReadTribe();

	ResourceTribe getTribe();

	ResourceTribe getWriteTribe();

	SliceResource get(long seed, Function function);

	SliceResource getAny(long seed);

	Long getSliceId();

	void setSliceId(Long sliceId);

	Long getRegisteredId();

	void setRegisteredId(Long registeredId);

	String getAlias();

	void setAlias(String alias);

	Failover getFailover();
}