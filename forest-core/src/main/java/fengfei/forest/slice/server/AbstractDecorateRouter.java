package fengfei.forest.slice.server;

import java.util.Map;

import fengfei.forest.slice.Detector;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public abstract class AbstractDecorateRouter<Key, R extends ServerResource>
		implements Router<Key, R> {

	protected Router<Key, SliceResource> decoratedRouter;

	public AbstractDecorateRouter(Router<Key, SliceResource> decoratedRouter) {
		super();
		this.decoratedRouter = decoratedRouter;
	}

	public Router<Key, SliceResource> getDecoratedRouter() {
		return decoratedRouter;
	}


	@Override
	public void register(Slice<Key> slice) {
		getDecoratedRouter().register(slice);
	}

	@Override
	public void remove(SliceResource resource) {
		getDecoratedRouter().remove(resource);
	}

	@Override
	public void remove(Long sliceId) {
		getDecoratedRouter().remove(sliceId);
	}

	@Override
	public Map<Long, Slice<Key>> getSlices() {
		return getDecoratedRouter().getSlices();
	}

	@Override
	public void register(Slice<Key> slice, Range... ranges) {
		getDecoratedRouter().register(slice, ranges);
	}

	@Override
	public void setOverflowType(OverflowType overflowType) {
		getDecoratedRouter().setOverflowType(overflowType);
	}

	@Override
	public void setEqualizer(Equalizer<Key> equalizer) {
		getDecoratedRouter().setEqualizer(equalizer);
	}

	@Override
	public void register(Long sliceId, String alias, SliceResource resource) {
		getDecoratedRouter().register(sliceId, alias, resource);
	}

	@Override
	public void register(SliceResource resource, String alias, Range... ranges) {
		getDecoratedRouter().register(resource, alias, ranges);
	}

	@Override
	public void register(Long sliceId, SliceResource resource) {
		getDecoratedRouter().register(sliceId, resource);
	}

	@Override
	public void register(SliceResource resource, Range... ranges) {
		getDecoratedRouter().register(resource, ranges);
	}

	@Override
	public Slice<Key> get(Long sliceId) {
		return getDecoratedRouter().get(sliceId);
	}

	@Override
	public void register(Resource resource) {
		getDecoratedRouter().register(resource);
	}

	@Override
	public void map(Long sliceId, String alias, String resourceName,
			Function function) {
		getDecoratedRouter().map(sliceId, alias, resourceName, function);
	}

	@Override
	public void map(Long sliceId, String resourceName, Function function) {
		getDecoratedRouter().map(sliceId, resourceName, function);
	}

	@Override
	public void map(String resourceName, Function function, Range... ranges) {
		getDecoratedRouter().map(resourceName, function, ranges);
	}

	@Override
	public void map(String resourceName, String alias, Function function,
			Range... ranges) {
		getDecoratedRouter().map(resourceName, alias, function, ranges);
	}

	@Override
	public OverflowType getOverflowType() {
		return getDecoratedRouter().getOverflowType();
	}

	@Override
	public Detector getDetector() {
		return getDecoratedRouter().getDetector();
	}

	public void setDetector(Detector detector) {
		getDecoratedRouter().setDetector(detector);
	}

	@Override
	public void setPlotter(Plotter plotter) {
		getDecoratedRouter().setPlotter(plotter);
	}

	@Override
	public void registerChild(Router<Key, SliceResource> childRouter,
			Range... ranges) {
		getDecoratedRouter().registerChild(childRouter, ranges);

	}

	@Override
	public void registerChild(Long sliceId,
			Router<Key, SliceResource> childRouter) {
		getDecoratedRouter().registerChild(sliceId, childRouter);
	}

	@Override
	public Map<String, Resource> getResourceMap() {

		return decoratedRouter.getResourceMap();
	}

	@Override
	public void registerGlobal(Resource resource) {
		decoratedRouter.registerGlobal(resource);

	}

	@Override
	public Map<String, Resource> getGlobalResources() {
		return decoratedRouter.getGlobalResources();
	}

}
