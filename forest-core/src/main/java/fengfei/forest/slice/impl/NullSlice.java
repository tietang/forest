package fengfei.forest.slice.impl;

import java.util.Map;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
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
	public void setParams(Map<String, String> extraInfo) {

	}

	@Override
	public void addParams(String key, String value) {

	}

	@Override
	public Map<String, String> getParams() {

		return null;
	}

	@Override
	public void addParams(Map<String, String> extraInfo) {

	}

	@Override
	public void add(SliceResource resource) {

	}

	@Override
	public void add(SliceResource resource, Function function) {

	}

	@Override
	public void remove(SliceResource resource) {

	}

	@Override
	public SliceResource get(long seed, Function function) {

		return null;
	}

	@Override
	public SliceResource getAny(long seed) {

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
