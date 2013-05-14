package fengfei.forest.slice;

import java.util.Map;

import fengfei.forest.slice.SliceResource.Function;

public interface SliceResourceRegistry {

    void register(Resource resource);

    void registerGlobal(Resource resource);

    void map(Long sliceId, String alias, String resourceName, Function function);

    void map(Long sliceId, String resourceName, Function function);

    void map(String resourceName, Function function, Range... ranges);

    void map(String resourceName, String alias, Function function, Range... ranges);

    Map<String, Resource> getResourceMap();

    Map<String, Resource> getGlobalResources();
}
