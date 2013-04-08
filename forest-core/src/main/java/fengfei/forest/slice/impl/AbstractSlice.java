package fengfei.forest.slice.impl;

import java.util.HashMap;
import java.util.Map;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;

/**
 * 
 * @author wtt
 * 
 * @param <Key>
 */
public abstract class AbstractSlice<Key> implements Slice<Key> {

	protected Long sliceId;
	protected String alias;
	protected Map<String, String> params = new HashMap<>();
	protected Router<Key> childRouter;

	public AbstractSlice(Long sliceId) {
		setSliceId(sliceId);
		setAlias(String.valueOf(sliceId));
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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
		this.params = extraInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.slice.ResourceSet#addExtraInfo(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addParams(String key, String value) {
		params.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.slice.ResourceSet#getExtraInfo()
	 */
	@Override
	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public void addParams(Map<String, String> extraInfo) {
		if (extraInfo == null) {
			return;
		}
		this.params.putAll(new HashMap<>(extraInfo));
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
