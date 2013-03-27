package fengfei.forest.slice.database;

import java.util.Map;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.Router;

public class DatabaseRouter<Key > implements
		Router<Key> {

	protected Router<Key> router;

	public DatabaseRouter(Router<Key> router) {
		super();
		this.router = router;
	}

	@Override
	public ServerResource locate(Key key, Function function) {
		return new ServerResource(router.locate(key, function));
	}

	@Override
	public ServerResource locate(Key key) {
		return new ServerResource(router.locate(key));
	}

	@Override
	public ServerResource first() {
		return new ServerResource(router.first());
	}

	@Override
	public ServerResource first(Function function) {
		return new ServerResource(router.first(function));
	}

	@Override
	public ServerResource last() {
		return new ServerResource(router.last());
	}

	@Override
	public ServerResource last(Function function) {
		return new ServerResource(router.last(function));
	}

	@Override
	public void register(Slice<Key> slice) {
		router.register(slice);
	}

	@Override
	public void register(Long sliceId, SliceResource resource) {
		router.register(sliceId, resource);
	}

	@Override
	public void update(Long sliceId, SliceResource resource) {
		router.update(sliceId, resource);
	}

	@Override
	public void remove(SliceResource resource) {
		router.remove(resource);
	}

	@Override
	public void remove(Long sliceId) {
		router.remove(sliceId);
	}

	@Override
	public void register(SliceResource resource, Range... ranges) {
		router.register(resource, ranges);
	}

	@Override
	public Map<Long, Slice<Key>> getSlices() {
		return router.getSlices();
	}

	@Override
	public void register(Slice<Key> slice, Range... ranges) {
		router.register(slice, ranges);

	}

	@Override
	public void setOverflowType(OverflowType overflowType) {
		router.setOverflowType(overflowType);
	}

	@Override
	public void setSelectType(SelectType selectType) {
		router.setSelectType(selectType);

	}

	@Override
	public void setEqualizer(Equalizer<Key> equalizer) {
		router.setEqualizer(equalizer);

	}
}
