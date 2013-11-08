package fengfei.forest.slice.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class ServerRouter<Key> extends
		AbstractDecorateRouter<Key, ServerResource> implements Router<Key, ServerResource> {

	protected Router<Key, SliceResource> router;

	public ServerRouter(Router<Key, SliceResource> router) {
		super(router);
	}


	@Override
	public ServerResource locate(Key key, Function function) {
		return new ServerResource(getDecoratedRouter().locate(key, function));
	}

	@Override
	public ServerResource locate(Key key) {
		return new ServerResource(getDecoratedRouter().locate(key));
	}

	@Override
	public ServerResource first() {
		return new ServerResource(getDecoratedRouter().first());
	}

	@Override
	public ServerResource first(Function function) {
		return new ServerResource(getDecoratedRouter().first(function));
	}

	@Override
	public ServerResource last() {
		return new ServerResource(getDecoratedRouter().last());
	}

	@Override
	public ServerResource last(Function function) {
		return new ServerResource(getDecoratedRouter().last(function));
	}

	@Override
	public Map<ServerResource, List<Key>> groupLocate(Function function,
			List<Key> keys) {
		Map<ServerResource, List<Key>> sliceResourceMap = new HashMap<>();
		for (Key key : keys) {
			ServerResource sr = locate(key, function);
			if (sr != null) {
				List<Key> ks = sliceResourceMap.get(sr);
				if (ks == null) {
					ks = new ArrayList<>();
				}
				ks.add(key);
				sliceResourceMap.put(sr, ks);
			}
		}
		return sliceResourceMap;
	}

	@Override
	public Map<ServerResource, List<Key>> groupLocate(List<Key> keys) {
		return groupLocate(keys);
	}
}
