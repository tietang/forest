package fengfei.forest.slice.impl;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Resource.Function;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.exception.NonExistedResourceException;

/**
 * 
 * 
 * *
 * 
 * <pre>
 *  通过注册的范围来匹配：对输入的Key值，通过 Equalizer.get(Key, int)计算后在注册的范围来匹配
 *  
 *  例如已注册的：
 *  100=Slice1[Resource1,Resource2] # 范围 1-100
 *  200=Slice2[Resource3,Resource4] # 范围 101-200
 *  300=Slice3[Resource3,Resource4] # 范围 201-300
 *  400=Slice4[Resource5,Resource6] # 范围 301-400
 * 
 *  Equalizer.get(Key, int) = 3， 那么 *  NavigableRouter.locate() = one of 100=Slice1[Resource1,Resource2]
 *  Equalizer.get(Key, int) = 223， 那么 *  NavigableRouter.locate() = one of 300=Slice3[Resource3,Resource4]
 * </pre>
 * 
 * @param <Key>
 */
public class NavigableRouter<Key> extends AbstractRouter<Key> {

	protected NavigableMap<Long, Slice<Key>> slices = new ConcurrentSkipListMap<>();

	public NavigableRouter() {
		super();
	}

	public NavigableRouter(Equalizer<Key> equalizer) {
		super(equalizer);
	}

	private Resource getResource(Map.Entry<Long, Slice<Key>> entry, Key key,
			Function function, long id, boolean isDealOver) {
		if (slices.size() == 0) {
			throw new NonExistedResourceException("id=" + id
					+ " non-existed Slice.");
		}
		if (entry == null || entry.getValue() == null
				|| entry.getValue() instanceof NullSlice) {
			return dealOverflow(key, function, id, isDealOver);
		}
		Slice<Key> slice = entry.getValue();
		//
		//
		Resource resource = slice.get(id, function);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return router.locate(key, function);
		}
		return resource;
	}

	private Resource getResource(Map.Entry<Long, Slice<Key>> entry, Key key,
			long id, boolean isDealOver) {
		if (slices.size() == 0) {
			throw new NonExistedResourceException(" non-existed slice for id="
					+ id);
		}
		if (entry == null || entry.getValue() == null
				|| entry.getValue() instanceof NullSlice) {
			return dealOverflow(key, null, id, isDealOver);
		}
		Slice<Key> slice = entry.getValue();
		Resource resource = slice.getAny(id);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return router.locate(key);
		}
		return resource;
	}

	private Random random = new Random(19800202);

	private Resource getResource(Map.Entry<Long, Slice<Key>> entry,
			Function function, boolean isFirst) {
		if (entry == null || entry.getValue() == null
				|| entry.getValue() instanceof NullSlice) {
			throw new NonExistedResourceException(" non-existed any slice.");
		}
		Slice<Key> slice = entry.getValue();
		Resource resource = function == null ? slice.getAny(random.nextLong())
				: slice.get(random.nextLong(), function);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return isFirst ? router.first() : router.last();
		}
		return resource;
	}

	public Resource first(Function function) {
		Map.Entry<Long, Slice<Key>> entry = slices.firstEntry();
		return getResource(entry, function, true);
	}

	@Override
	public Resource first() {
		Map.Entry<Long, Slice<Key>> entry = slices.firstEntry();
		return getResource(entry, null, true);
	}

	@Override
	public Resource last() {
		Map.Entry<Long, Slice<Key>> entry = slices.lastEntry();
		return getResource(entry, null, false);
	}

	public Resource last(Function function) {
		Map.Entry<Long, Slice<Key>> entry = slices.lastEntry();
		return getResource(entry, function, false);
	}

	@Override
	public Resource locate(Key key, Function function) {
		long id = equalizer.get(key, slices.size());
		Map.Entry<Long, Slice<Key>> entry = slices.ceilingEntry(id);
		return getResource(entry, key, function, id, true);
	}

	@Override
	public Resource locate(Key key) {
		long id = equalizer.get(key, slices.size());
		Map.Entry<Long, Slice<Key>> entry = slices.ceilingEntry(id);
		return getResource(entry, key, id, true);
	}

	@Override
	public Map<Long, Slice<Key>> getSlices() {
		return slices;
	}

	@Override
	public void addslice(Slice<Key> slice) {
		getSlices().put(slice.getSliceId(), slice);

	}

	public void register(Resource resource, Range... ranges) {
		for (Range range : ranges) {
			long previous = range.start - 1;
			if (previous > 0) {
				Slice<Key> startslice = slices.get(previous);
				if (startslice == null) {
					Slice<Key> nullslice = new NullSlice<>();
					slices.put(previous, nullslice);
				}
			}
			//
			register(range.end, resource);
		}
	}

	@Override
	public void register(Slice<Key> slice, Range... ranges) {
		for (Range range : ranges) {
			if (range.end == range.start) {
				slices.put(range.end, slice);
			} else {
				long previous = range.start - 1;
				if (previous > 0) {
					Slice<Key> startslice = slices.get(previous);
					if (startslice == null) {
						Slice<Key> nullslice = new NullSlice<>();
						slices.put(previous, nullslice);
					}
				}
			}
			slices.put(range.end, slice);
		}

	}

	@Override
	public String toString() {
		return "NavigableRouter [\n slices=" + slices + ", equalizer="
				+ equalizer + ", overflowType=" + overflowType
				+ ", selectType=" + selectType + ", defaultExtraInfo="
				+ defaultExtraInfo + "]";
	}

	@Override
	public boolean isSupported(OverflowType overflowType) {
		return overflowType == OverflowType.Exception
				|| overflowType == OverflowType.First
				|| overflowType == OverflowType.Last;
	}

}
