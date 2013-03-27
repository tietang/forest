package fengfei.forest.slice;

import java.util.Map;

public interface SliceRegistry<Key> {

	void register(Slice<Key> slice);

	void register(Long sliceId, String alias, SliceResource resource);

	void register(SliceResource resource, String alias, Range... ranges);

	void register(Slice<Key> slice, Range... ranges);

	void update(Long sliceId, SliceResource resource);

	void remove(SliceResource resource);

	void remove(Long sliceId);

	Map<Long, Slice<Key>> getSlices();
}