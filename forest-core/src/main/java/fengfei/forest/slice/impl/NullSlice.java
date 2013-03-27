package fengfei.forest.slice.impl;

import java.util.Map;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Resource.Function;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;

public class NullSlice<Key> implements Slice<Key> {

	@Override
	public Router<Key> getChildRouter() {

		return null;
	}

	@Override
	public void setChildRouter(Router<Key> childRouter) {

	}

	@Override
	public void setPlotter(Plotter plotter) {

	}

	@Override
	public void setExtraInfo(Map<String, String> extraInfo) {

	}

	@Override
	public void addExtraInfo(String key, String value) {

	}

	@Override
	public Map<String, String> getExtraInfo() {

		return null;
	}

	@Override
	public void addExtraInfo(Map<String, String> extraInfo) {

	}

	@Override
	public void add(Resource resource) {

	}

	@Override
	public void add(Resource resource, Function function) {

	}

	@Override
	public void remove(Resource resource) {

	}

	@Override
	public Resource get(long seed, Function function) {

		return null;
	}

	@Override
	public Resource getAny(long seed) {

		return null;
	}

	@Override
	public Long getSliceId() {

		return null;
	}

	@Override
	public void setSliceId(Long sliceId) {

	}

}
