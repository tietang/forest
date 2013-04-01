package fengfei.forest.slice.server.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.server.ServerResource;
import fengfei.forest.slice.server.ServerRouter;

public class PoolableServerRouter<Key, D> extends ServerRouter<Key> {

	private Map<String, PooledSource<D>> pooledDataSources = new ConcurrentHashMap<>();
	private PoolableSourceFactory<D> poolableSourceFactory;

	public PoolableServerRouter(
			Router<Key> router,
			PoolableSourceFactory<D> poolableSourceFactory) {
		super(router);
		this.poolableSourceFactory = poolableSourceFactory;
	}

	public Map<String, PooledSource<D>> getPooledDataSources() {
		return pooledDataSources;
	}

	@Override
	public PoolableServerResource<D> locate(Key key) {
		ServerResource serverResource = super.locate(key);
		PoolableServerResource<D> resource = new PoolableServerResource<>(serverResource);
		return locate(resource);
	}

	@Override
	public PoolableServerResource<D> locate(Key key, Function function) {
		ServerResource serverResource = super.locate(key, function);
		PoolableServerResource<D> resource = new PoolableServerResource<>(serverResource);
		return locate(resource);
	}

	private PoolableServerResource<D> locate(PoolableServerResource<D> res) {
		String name = res.getName();
		PooledSource<D> source = pooledDataSources.get(name);
		if (source == null) {
			try {
				source = poolableSourceFactory.createDataSource(res);
				pooledDataSources.put(name, source);
			} catch (PoolableException e) {
				throw new SliceRuntimeException(
						"Can't create datasource for the slice " + res,
						e);
			}
		}
		if (source == null) {
			throw new NonExistedSliceException("Can't get datasource for the slice" + res);
		}
		res.setPooledSource(source);
		return res;
	}
}
