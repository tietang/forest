package fengfei.forest.slice;

import fengfei.forest.slice.SliceResource.Function;

public interface SliceResourceRegistry {

	void register(Resource resource);

	void map(Long sliceId, String alias, String resourceName, Function function);

	void map(Long sliceId, String resourceName, Function function);

	void map(String resourceName, Function function, Range... ranges);

	void map(String resourceName, String alias, Function function, Range... ranges);
}
