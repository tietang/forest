package fengfei.forest.slice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fengfei.forest.slice.Failover;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.Status;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.exception.UnSupportedException;
import fengfei.forest.slice.plotter.HashPlotter;

/**
 * 
 * 
 * 
 * @param <Key>
 */
public class ReadWriteSlice<Key> implements Slice<Key> {
	protected Long sliceId;
	protected String alias;
	protected Map<String, String> params = new HashMap<>();
	protected Router<Key,SliceResource> childRouter;

	protected List<SliceResource> resources = new ArrayList<>();
	protected Lock lock = new ReentrantLock();
	private ResourceTribe read = new ResourceTribe();
	private ResourceTribe write = new ResourceTribe();
	private ResourceTribe all = new ResourceTribe();
	protected Plotter plotter = new HashPlotter();

	public ReadWriteSlice(Long sliceId) {
		setSliceId(sliceId);
		setAlias(String.valueOf(sliceId));
	}

	public ReadWriteSlice(Long sliceId, Plotter plotter) {
		this(sliceId);
		setPlotter(plotter);
	}

	@Override
	public void setPlotter(Plotter plotter) {
		this.plotter = plotter;
	}

	public void add(SliceResource resource) {
		mergeInheritInfoTo(resource);
		resource.setSliceId(sliceId);
		resource.setAlias(alias);
		Function function = resource.getFunction();
		switch (function) {
		case Read:
			read.addResource(resource);
			break;
		case Write:
			write.addResource(resource);
			break;
		case ReadWrite:
			read.addResource(resource);
			write.addResource(resource);

			break;
		default:
			throw new UnSupportedException("unsupported the function: "
					+ function.name());
		}
		all.addResource(resource);
		resources.add(resource);
	}

	@Override
	public void remove(SliceResource resource) {
		lock.lock();
		try {
			read.removeResource(resource);
			write.removeResource(resource);
			all.removeResource(resource);
			resources.remove(resource);
		} catch (Exception e) {
			throw new SliceRuntimeException("remove resource error.", e);
		} finally {
			lock.unlock();
		}
	}

	public SliceResource get(long seed, Function function) {
		switch (function) {
		case Read:
			return next(seed, read);
		case Write:
			return next(seed, write);
		case ReadWrite:
			return next(seed, all);
		default:
			break;
		}
		return null;
	}

	@Override
	public SliceResource getAny(long seed) {
		return next(seed, all);
	}

	protected SliceResource next(long seed, ResourceTribe tribe) {

		return plotter.to(seed, tribe.getAvailableResources(),
				tribe.getFailResources());
	}

	public ResourceTribe getReadTribe() {
		return read;
	}

	public ResourceTribe getTribe() {
		return all;
	}

	public ResourceTribe getWriteTribe() {
		return write;
	}

	public List<SliceResource> getResources() {
		return resources;
	}

	public boolean fail(SliceResource resource) {
		lock.lock();
		try {
			resource.setStatus(Status.Error);
			return (fail(read, resource) || fail(write, resource))
					&& fail(all, resource);
		} catch (Exception e) {
			throw new SliceRuntimeException("fail resource error.", e);
			// return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean fail(ResourceTribe tribe, SliceResource resource) {

		List<SliceResource> availableResources = tribe.getAvailableResources();
		List<SliceResource> failResources = tribe.getFailResources();
		availableResources.remove(resource);
		failResources.add(resource);

		return true;
	}

	public boolean recover(SliceResource resource) {
		lock.lock();
		try {
			resource.setStatus(Status.Normal);
			return (recover(read, resource) || recover(write, resource))
					&& recover(all, resource);
		} catch (Exception e) {
			throw new SliceRuntimeException("recover resource error.", e);
			// return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean recover(ResourceTribe tribe, SliceResource resource) {
		List<SliceResource> availableResources = tribe.getAvailableResources();
		List<SliceResource> failResources = tribe.getFailResources();
		failResources.remove(resource);
		availableResources.add(resource);
		return true;
	}

	@Override
	public String toString() {
		return "ReadWriteSlice [read=" + read + ", write=" + write + ", all="
				+ all + ", sliceId=" + sliceId + ", alias=" + alias
				+ ", params=" + params + ", childRouter=" + childRouter + "]";
	}

	@Override
	public Failover getFailover() {
		return this;
	}

	Long registeredId;

	public Long getRegisteredId() {
		return registeredId;
	}

	public void setRegisteredId(Long registeredId) {
		this.registeredId = registeredId;
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

	public Router<Key,SliceResource> getChildRouter() {
		return childRouter;
	}

	public void setChildRouter(Router<Key,SliceResource> childRouter) {
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

	protected void mergeInheritInfoTo(SliceResource resource) {
		Map<String, String> extraInfo = new HashMap<String, String>(
				resource.getExtraInfo());
		resource.addParams(getParams());
		resource.addParams(extraInfo);
	}
}
