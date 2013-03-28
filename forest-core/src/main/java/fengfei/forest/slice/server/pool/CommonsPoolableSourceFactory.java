package fengfei.forest.slice.server.pool;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.pool.impl.GenericObjectPool;

import fengfei.forest.slice.server.ServerResource;

public class CommonsPoolableSourceFactory<D> implements PoolableSourceFactory<D> {

	CommonsPoolableObjectFactory<D> poolableObjectFactory;

	public CommonsPoolableSourceFactory(CommonsPoolableObjectFactory<D> poolableObjectFactory) {
		super();
		this.poolableObjectFactory = poolableObjectFactory;
	}

	@Override
	public PooledSource<D> createDataSource(ServerResource resource) throws PoolableException {
		try {
			GenericObjectPool.Config config = new GenericObjectPool.Config();
			BeanUtils.copyProperties(config, resource.getParams());
			poolableObjectFactory.setHost(resource.getHost());
			poolableObjectFactory.setPort(resource.getPort());
			poolableObjectFactory.setSchema(resource.getSchema());
			poolableObjectFactory.setUsername(resource.getUsername());
			poolableObjectFactory.setPassword(resource.getPassword());
			poolableObjectFactory.setParams(resource.getParams());
			CommonsPooledSource<D> pooledSource = new CommonsPooledSource<>(
					poolableObjectFactory,
					config);
			return pooledSource;
		} catch (Exception e) {
			throw new PoolableException("create PooledSource error. ", e);
		}
	}

	@Override
	public void destory(PooledSource<D> source) throws PoolableException {
		source.close();
	}
}
