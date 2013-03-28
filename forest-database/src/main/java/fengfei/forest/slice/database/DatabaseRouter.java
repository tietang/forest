package fengfei.forest.slice.database;

import java.util.Map;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.Slice;

public class DatabaseRouter<Key> implements Router<Key> {

	protected Router<Key> router;

	public DatabaseRouter(Router<Key> router) {
		super();
		this.router = router;
	}

	public Router<Key> getRouter() {
		return router;
	}

	@Override
	public ServerResource locate(Key key, Function function) {
		return new ServerResource(getRouter().locate(key, function));
	}

	@Override
	public ServerResource locate(Key key) {
		return new ServerResource(getRouter().locate(key));
	}

	@Override
	public ServerResource first() {
		return new ServerResource(getRouter().first());
	}

	@Override
	public ServerResource first(Function function) {
		return new ServerResource(getRouter().first(function));
	}

	@Override
	public ServerResource last() {
		return new ServerResource(getRouter().last());
	}

	@Override
	public ServerResource last(Function function) {
		return new ServerResource(getRouter().last(function));
	}

	@Override
	public void register(Slice<Key> slice) {
		getRouter().register(slice);
	}

	@Override
	public void remove(SliceResource resource) {
		getRouter().remove(resource);
	}

	@Override
	public void remove(Long sliceId) {
		getRouter().remove(sliceId);
	}

	@Override
	public Map<Long, Slice<Key>> getSlices() {
		return getRouter().getSlices();
	}

	@Override
	public void register(Slice<Key> slice, Range... ranges) {
		getRouter().register(slice, ranges);
	}

	@Override
	public void setOverflowType(OverflowType overflowType) {
		getRouter().setOverflowType(overflowType);
	}

	@Override
	public void setSelectType(SelectType selectType) {
		getRouter().setSelectType(selectType);
	}

	@Override
	public void setEqualizer(Equalizer<Key> equalizer) {
		getRouter().setEqualizer(equalizer);
	}

	@Override
	public void register(Long sliceId, String alias, SliceResource resource) {
		getRouter().register(sliceId, alias, resource);
	}

	@Override
	public void register(SliceResource resource, String alias, Range... ranges) {
		getRouter().register(resource, alias, ranges);
	}

	@Override
	public void register(Long sliceId, SliceResource resource) {
		getRouter().register(sliceId, resource);
	}

	@Override
	public void register(SliceResource resource, Range... ranges) {
		getRouter().register(resource, ranges);
	}

	@Override
	public Slice<Key> get(Long sliceId) {
		return getRouter().get(sliceId);
	}

	@Override
	public void register(Resource resource) {
		getRouter().register(resource);
	}

	@Override
	public void map(Long sliceId, String alias, String resourceName, Function function) {
		getRouter().map(sliceId, alias, resourceName, function);
	}

	@Override
	public void map(Long sliceId, String resourceName, Function function) {
		getRouter().map(sliceId, resourceName, function);
	}

	@Override
	public void map(String resourceName, Function function, Range... ranges) {
		getRouter().map(resourceName, function, ranges);
	}

	@Override
	public void map(String resourceName, String alias, Function function, Range... ranges) {
		getRouter().map(resourceName, alias, function, ranges);
	}
}
