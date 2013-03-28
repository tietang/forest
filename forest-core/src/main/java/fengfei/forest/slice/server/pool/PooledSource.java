package fengfei.forest.slice.server.pool;

public interface PooledSource<D> {

	D getDource() throws PoolableException;

	void close(D d) throws PoolableException;

	void close() throws PoolableException;
}
