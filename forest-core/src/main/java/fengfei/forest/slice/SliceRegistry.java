package fengfei.forest.slice;

import java.util.Map;

public interface SliceRegistry<Key> {

	void register(Slice<Key> slice);

	void register(Long sliceId, Resource resource);

	void register(Resource resource, Range... ranges);

	void register(Slice<Key> slice, Range... ranges);

	void update(Long sliceId, Resource resource);

	void remove(Resource resource);

	void remove(Long sliceId);

	Map<Long, Slice<Key>> getSlices();
}