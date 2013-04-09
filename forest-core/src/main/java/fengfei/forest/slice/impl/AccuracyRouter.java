package fengfei.forest.slice.impl;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedSliceException;

/**
 * 
 * <pre>
 *  精确匹配，对输入的Key值，通过 Equalizer.get(Key, int)计算后精确匹配所注册的 slice。
 *  要求所注册的sliceId为连续的，且最大sliceId = slice Length，如果中间有不连续，将会导致部分slice无法找到
 *  
 *  例如註冊：
 *  1=Slice1[Resource1,Resource2]
 *  2=Slice2[Resource3,Resource4]
 *  3=Slice3[Resource3,Resource4]
 *  4=Slice4[Resource5,Resource6]
 * 
 *  Equalizer.get(Key, int) = 3
 *  AccuracyNavigator.locate() = one of Slice3[Resource3,Resource4]
 * </pre>
 * 
 * 
 * 
 * @param <Key>
 */
public class AccuracyRouter<Key> extends AbstractRouter<Key> {

	protected Map<Long, Slice<Key>> slices = new ConcurrentHashMap<>();
	protected NavigableMap<Long, Slice<Key>> sortedSlices = new ConcurrentSkipListMap<>();

	public AccuracyRouter() {
		super();
	}

	public AccuracyRouter(Equalizer<Key> equalizer) {
		super(equalizer);
	}

	public AccuracyRouter(Equalizer<Key> equalizer,
			Map<String, String> defaultExtraInfo) {
		this(equalizer);
		this.defaultExtraInfo = defaultExtraInfo;
	}

	public AccuracyRouter(Equalizer<Key> equalizer, Plotter plotter) {
		super(equalizer, plotter);

	}

	public AccuracyRouter(Equalizer<Key> equalizer, Plotter plotter,
			Map<String, String> defaultExtraInfo) {
		super(equalizer, plotter, defaultExtraInfo);
	}

	@Override
	public SliceResource locate(Key key, Function function) {
		long id = equalizer.get(key, slices.size());
		Slice<Key> slice = slices.get(id);
		return getResource(slice, key, function, id, true);
	}

	@Override
	public SliceResource locate(Key key) {
		long id = equalizer.get(key, slices.size());
		Slice<Key> slice = slices.get(id);
		return getResource(slice, key, id, true);
	}

	private Random random = new Random(19800202);

	private SliceResource getResource(Map.Entry<Long, Slice<Key>> entry,
			Function function, boolean isFirst) {
		if (entry == null || entry.getValue() == null
				|| entry.getValue() instanceof NullSlice) {
			throw new NonExistedSliceException(" non-existed any slice for "
					+ (isFirst ? "first slice" : "last slice"));
		}
		Slice<Key> slice = entry.getValue();

		SliceResource resource = function == null ? slice.getAny(random
				.nextLong()) : slice.get(random.nextLong(), function);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return isFirst ? router.first() : router.last();
		}
		return resource;
	}

	public SliceResource first(Function function) {
		Map.Entry<Long, Slice<Key>> entry = sortedSlices.firstEntry();
		return getResource(entry, function, true);
	}

	@Override
	public SliceResource first() {
		Map.Entry<Long, Slice<Key>> entry = sortedSlices.firstEntry();
		return getResource(entry, null, true);
	}

	@Override
	public SliceResource last() {
		Map.Entry<Long, Slice<Key>> entry = sortedSlices.lastEntry();
		return getResource(entry, null, false);
	}

	public SliceResource last(Function function) {
		Map.Entry<Long, Slice<Key>> entry = sortedSlices.lastEntry();
		return getResource(entry, function, false);
	}

	@Override
	public Map<Long, Slice<Key>> getSlices() {
		return slices;
	}

	public void addslice(Slice<Key> slice) {
		getSlices().put(slice.getSliceId(), slice);
		sortedSlices.put(slice.getSliceId(), slice);

		List<SliceResource> resources = slice.getResources();
		for (SliceResource resource : resources) {
			resAndSlices.put(resource.getName(), slice.getSliceId());
		}
	}

	@Override
	public void register(SliceResource resource, String alias, Range... ranges) {
		for (Range range : ranges) {
			for (long i = range.start; i <= range.end; i++) {
				register(i, alias, resource);
			}
		}
	}

	@Override
	public void register(Slice<Key> slice, Range... ranges) {
		for (Range range : ranges) {
			for (long i = range.start; i <= range.end; i++) {
				slices.put(i, slice);
				List<SliceResource> resources = slice.getResources();
				for (SliceResource resource : resources) {
					resAndSlices.put(resource.getName(), i);
				}
			}
		}
	}

	@Override
	public void registerChild(Router<Key> childRouter, Range... ranges) {
		for (Range range : ranges) {
			for (long i = range.start; i <= range.end; i++) {
				registerChild(Long.valueOf(i), childRouter);
			}
		}
	}

	@Override
	public void map(String resourceName, String alias, Function function,
			Range... ranges) {
		for (Range range : ranges) {
			for (long i = range.start; i <= range.end; i++) {
				map(i, alias, resourceName, function);
			}
		}
	}

	@Override
	public String toString() {
		return "AccuracyRouter [slices=" + slices + ", sortedSlices="
				+ sortedSlices + "]";
	}

	@Override
	public boolean isSupported(OverflowType overflowType) {
		return overflowType == OverflowType.Exception
				|| overflowType == OverflowType.First
				|| overflowType == OverflowType.Last;
	}
}
