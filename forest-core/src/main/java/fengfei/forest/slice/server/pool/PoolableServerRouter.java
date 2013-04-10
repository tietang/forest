package fengfei.forest.slice.server.pool;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.server.ServerResource;
import fengfei.forest.slice.server.ServerRouter;

public class PoolableServerRouter<Key, D> extends ServerRouter<Key> {
	static Logger log = LoggerFactory.getLogger(PoolableServerRouter.class);
	protected Map<String, PooledSource<D>> pooledDataSources = new ConcurrentHashMap<>();
	protected PoolableSourceFactory<D> poolableSourceFactory;

	public PoolableServerRouter(Router<Key> router,
			PoolableSourceFactory<D> poolableSourceFactory) {
		super(router);
		this.poolableSourceFactory = poolableSourceFactory;
	}

	public Map<String, PooledSource<D>> getPooledDataSources() {
		return pooledDataSources;
	}

	public void close() {
		Set<Entry<String, PooledSource<D>>> entries = pooledDataSources
				.entrySet();
		for (Entry<String, PooledSource<D>> entry : entries) {
			try {
				entry.getValue().close();
			} catch (PoolableException e) {
				log.error("close source error for " + entry.getKey(), e);
			}
		}

	}

	@Override
	public PoolableServerResource<D> locate(Key key) {
		ServerResource serverResource = super.locate(key);
		PoolableServerResource<D> resource = new PoolableServerResource<>(
				serverResource);
		return locate(resource);
	}

	@Override
	public PoolableServerResource<D> locate(Key key, Function function) {
		ServerResource serverResource = super.locate(key, function);
		PoolableServerResource<D> resource = new PoolableServerResource<>(
				serverResource);
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
						"Can't create datasource for the slice " + res, e);
			}
		}
		if (source == null) {
			throw new NonExistedSliceException(
					"Can't get datasource for the slice" + res);
		}
		res.setPooledSource(source);
		return res;
	}

}
