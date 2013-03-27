package fengfei.forest.slice.impl;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.exception.UnSupportedException;

/**
 * 
 * 
 * 
 * @param <Key>
 */
public class ReadWriteSlice<Key> extends AbstractSlice<Key> {

	protected Lock lock = new ReentrantLock();
	private ResourceTribe read = new ResourceTribe();
	private ResourceTribe write = new ResourceTribe();
	private ResourceTribe all = new ResourceTribe();
	protected Plotter plotter = new HashPlotter();

	public ReadWriteSlice() {
		super();
		read = new ResourceTribe();
		write = new ResourceTribe();
		all = new ResourceTribe();
	}

	public ReadWriteSlice(Plotter plotter) {
		this();
		setPlotter(plotter);
	}

	@Override
	public void setPlotter(Plotter plotter) {
		this.plotter = plotter;
		read.setPlotter(plotter);
		write.setPlotter(plotter);
		all.setPlotter(plotter);
	}

	public void add(SliceResource resource, Function function) {
		mergeInheritInfoTo(resource);
		resource.setSliceId(sliceId);
		switch (function) {
		case Read:
			read.addResource(resource);
			all.addResource(resource);
			break;
		case Write:
			write.addResource(resource);
			all.addResource(resource);
			break;
		case ReadWrite:
			read.addResource(resource);
			write.addResource(resource);
			all.addResource(resource);
			break;
		default:
			throw new UnSupportedException("unsupported the function: "
					+ function.name());
		}
	}

	@Override
	public void remove(SliceResource resource) {
		lock.lock();
		try {
			read.removeResource(resource);
			write.removeResource(resource);
			all.removeResource(resource);
		} catch (Exception e) {
			throw new SliceRuntimeException("remove resource error.", e);
		} finally {
			lock.unlock();
		}
	}

	public SliceResource get(long seed, Function function) {
		switch (function) {
		case Read:
			return read.next(seed);
		case Write:
			return write.next(seed);
		case ReadWrite:
			return all.next(seed);
		default:
			break;
		}
		return null;
	}

	@Override
	public SliceResource getAny(long seed) {
		return all.next(seed);
	}

	public boolean fail(SliceResource resource) {
		lock.lock();
		try {
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
				+ all + ", plotter=" + plotter + ", sliceId=" + sliceId
				+ ", extraInfo=" + extraInfo + ", childRouter=" + childRouter
				+ "]";
	}

}
