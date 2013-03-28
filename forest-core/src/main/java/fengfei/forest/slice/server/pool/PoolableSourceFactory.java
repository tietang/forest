package fengfei.forest.slice.server.pool;

import fengfei.forest.slice.server.ServerResource;

public interface PoolableSourceFactory<D> {

	PooledSource<D> createDataSource(ServerResource resource) throws PoolableException;

	public void destory(PooledSource<D> source) throws PoolableException;
}
