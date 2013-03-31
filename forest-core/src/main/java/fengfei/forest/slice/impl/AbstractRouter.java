package fengfei.forest.slice.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedResourceException;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.UnSupportedException;

public abstract class AbstractRouter<Key> implements Router<Key> {

	protected Equalizer<Key> equalizer;
	protected OverflowType overflowType = OverflowType.Last;
	protected SelectType selectType;
	protected Map<String, String> defaultExtraInfo = new HashMap<>();
	protected AtomicLong sliceIdCreator = new AtomicLong();
	protected Map<String, Resource> resources = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public AbstractRouter() {
		equalizer = (Equalizer<Key>) new LongEqualizer();
	}

	public AbstractRouter(Equalizer<Key> equalizer) {
		this.equalizer = equalizer;
	}

	public AbstractRouter(Equalizer<Key> equalizer, SelectType selectType) {
		super();
		this.equalizer = equalizer;
		this.selectType = selectType;
	}

	protected SliceResource dealOverflow(
			Key key,
			Function function,
			long id,
			boolean isDealOver) {
		if (!isSupported(overflowType)) {
			throw new UnSupportedException("unSupported overflowType :" + overflowType);
		}
		if (!isDealOver) {
			throw new NonExistedSliceException("id=" + id + " non-existed slice.");
		}
		switch (overflowType) {
		case First:
			return function == null ? first() : first(function);
		case Last:
			return function == null ? last() : last(function);
		case New:
			return null;
		case Exception:
			throw new NonExistedSliceException("id=" + id + " non-existed slice.");
		default:
			throw new NonExistedSliceException("id=" + id + " non-existed slice.");
		}
	}

	/**
	 * get a special function slice of logicslice
	 * 
	 * @param logicSlice
	 * @param key
	 * @param function
	 * @param id
	 * @param isDealOver
	 * @return
	 */
	protected SliceResource getResource(
			Slice<Key> slice,
			Key key,
			Function function,
			long id,
			boolean isDealOver) {
		if (slice == null) {
			return dealOverflow(key, function, id, isDealOver);
		}
		SliceResource resource = slice.get(id, function);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return router.locate(key);
		}
		return resource;
	}

	protected
			SliceResource
			getResource(Slice<Key> slice, Key key, long id, boolean isDealOver) {
		if (slice == null || slice instanceof NullSlice) {
			return dealOverflow(key, null, id, isDealOver);
		}
		SliceResource resource = slice.getAny(id);
		if (resource == null) {
			Router<Key> router = slice.getChildRouter();
			return router.locate(key);
		}
		return resource;
	}

	protected AtomicLong ids = new AtomicLong();

	public void setEqualizer(Equalizer<Key> equalizer) {
		this.equalizer = equalizer;
	}

	public void setOverflowType(OverflowType overflowType) {
		if (!isSupported(overflowType)) {
			throw new UnSupportedException("unSupported overflowType :" + overflowType);
		}
		this.overflowType = overflowType;
	}

	public OverflowType getOverflowType() {
		return overflowType;
	}

	public void setSelectType(SelectType selectType) {
		this.selectType = selectType;
	}

	public Map<String, String> getDefaultExtraInfo() {
		return defaultExtraInfo;
	}

	public void setDefaultExtraInfo(Map<String, String> defaultExtraInfo) {
		this.defaultExtraInfo = defaultExtraInfo;
	}

	@Override
	public void register(Slice<Key> slice) {
		addslice(slice);
	}

	protected Slice<Key> updateNullSlice(Slice<Key> slice, String alias) {
		if (slice != null && slice instanceof NullSlice) {
			Long sliceId = slice.getSliceId();
			getSlices().remove(sliceId);
			slice = null;
			slice = createSlice(sliceId, alias);
		}
		return slice;
	}

	@Override
	public void register(Long sliceId, String alias, SliceResource resource) {
		Slice<Key> slice = getSlices().get(sliceId);
		if (slice == null) {
			slice = createSlice(sliceId, alias);
		}
		slice.setSliceId(sliceId);
		update(slice, alias, resource);
	}

	@Override
	public void register(Long sliceId, SliceResource resource) {
		register(sliceId, null, resource);
	}

	@Override
	public void register(SliceResource resource, Range... ranges) {
		register(resource, null, ranges);
	}

	protected Slice<Key> createSlice(Long sliceId, String alias) {
		Slice<Key> slice = new ReadWriteSlice<>(sliceId);
		if (alias == null || "".equals(alias)) {
			slice.setAlias(String.valueOf(sliceIdCreator.getAndIncrement()));
		} else {
			slice.setAlias(alias);
		}
		return slice;
	}

	private void update(Slice<Key> slice, String alias, SliceResource resource) {
		slice = updateNullSlice(slice, alias);
		Map<String, String> extraInfo = new HashMap<>(getDefaultExtraInfo());
		extraInfo.putAll(slice.getParams());
		extraInfo.putAll(resource.getExtraInfo());
		resource.addParams(extraInfo);
		resource.setAlias(slice.getAlias());
		slice.add(resource);
		addslice(slice);
	}

	public abstract void addslice(Slice<Key> slice);

	public void update(Long sliceId, SliceResource resource) {
		Slice<Key> slice = getSlices().get(sliceId);
		if (slice == null) {
			throw new NonExistedSliceException("Non Existed slice for slice id:" + sliceId);
		}
		update(slice, slice.getAlias(), resource);
	}

	@Override
	public void remove(SliceResource resource) {
		if (resource == null || resource.getSliceId() == null) {
			throw new IllegalArgumentException("arg resource is imperfect.");
		} else if (getSlices().containsKey(resource.getSliceId())) {
			Slice<Key> slice = getSlices().get(resource.getSliceId());
			slice.remove(resource);
		} else {
			throw new NonExistedSliceException(
					"Can't remove,non existed slice for slice id:" + resource.getSliceId());
		}
	}

	@Override
	public void remove(Long sliceId) {
		if (sliceId == null) {
			throw new IllegalArgumentException("arg resource is imperfect.");
		} else if (getSlices().containsKey(sliceId)) {
			getSlices().remove(sliceId);
		} else {
			throw new NonExistedSliceException(
					"Can't remove, non existed slice for slice id:" + sliceId);
		}
	}

	public abstract boolean isSupported(OverflowType overflowType);

	@Override
	public void register(Resource resource) {
		resources.put(resource.getName(), resource);
	}

	@Override
	public void map(Long sliceId, String alias, String resourceName, Function function) {
		Slice<Key> slice = getSlices().get(sliceId);
		if (slice == null) {
			slice = createSlice(sliceId, alias);
		}
		if (alias == null) {
			alias = slice.getAlias();
		}
		Resource resource = resources.get(resourceName);
		if (resource == null) {
			throw new NonExistedResourceException("can't register resource: " + resourceName);
		}
		update(slice, slice.getAlias(), new SliceResource(sliceId, function, resource));
	}

	public void map(Long sliceId, String resourceName, Function function) {
		map(sliceId, String.valueOf(sliceId), resourceName, function);
	}

	public void map(String resourceName, Function function, Range... ranges) {
		map(resourceName, null, function, ranges);
	}

	@Override
	public Slice<Key> get(Long sliceId) {
		return getSlices().get(sliceId);
	}
}
