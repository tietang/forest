package fengfei.forest.slice.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import fengfei.forest.slice.Failover;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;

/**
 * 
 * @author wtt
 * 
 * @param <Key>
 */
public abstract class AbstractSlice<Key> implements Failover, Slice<Key> {

	protected Long sliceId;
	protected Map<String, String> extraInfo = new HashMap<>();

	protected Router<Key> childRouter;
	protected AtomicLong sliceIds = new AtomicLong();
	protected AtomicLong ids = new AtomicLong();

	public AbstractSlice() {
		setSliceId(sliceIds.getAndIncrement());
	}

	public Long getSliceId() {
		return sliceId;
	}

	public void setSliceId(Long sliceId) {
		this.sliceId = sliceId;
	}

	public Router<Key> getChildRouter() {
		return childRouter;
	}

	public void setChildRouter(Router<Key> childRouter) {
		this.childRouter = childRouter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.slice.ResourceSet#setExtraInfo(java.util.Map)
	 */
	@Override
	public void setParams(Map<String, String> extraInfo) {
		this.extraInfo = extraInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.slice.ResourceSet#addExtraInfo(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addParams(String key, String value) {
		extraInfo.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.slice.ResourceSet#getExtraInfo()
	 */
	@Override
	public Map<String, String> getParams() {
		return extraInfo;
	}

	@Override
	public void addParams(Map<String, String> extraInfo) {
		if (extraInfo == null) {
			return;
		}
		this.extraInfo.putAll(new HashMap<>(extraInfo));
	}

	@Override
	public void add(SliceResource resource) {
		add(resource, resource.getFunction());
	}

	protected void mergeInheritInfoTo(SliceResource resource) {
		Map<String, String> extraInfo = new HashMap<String, String>(
				resource.getExtraInfo());
		resource.addParams(getParams());
		resource.addParams(extraInfo);
	}
}
