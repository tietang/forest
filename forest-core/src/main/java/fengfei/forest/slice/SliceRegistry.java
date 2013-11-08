package fengfei.forest.slice;

import java.util.Map;

public interface SliceRegistry<Key> {

    void register(Slice<Key> slice);

    void register(Slice<Key> slice, Range... ranges);

    void register(Long sliceId, String alias, SliceResource resource);

    void register(Long sliceId, SliceResource resource);

    void register(SliceResource resource, String alias, Range... ranges);

    void register(SliceResource resource, Range... ranges);

    void registerChild(Router<Key, SliceResource> childRouter, Range... ranges);

    void registerChild(Long sliceId, Router<Key, SliceResource> childRouter);

    // void update(Long sliceId, SliceResource resource);

    void remove(SliceResource resource);

    void remove(Long sliceId);

    Slice<Key> get(Long sliceId);

    // List<SliceResource> getResources();
    Map<Long, Slice<Key>> getSlices();
}