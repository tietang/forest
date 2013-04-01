package fengfei.forest.slice.server.pool;

public interface PooledSource<D> {
	/**
	 * borrow a Source from pool
	 * 
	 * @return
	 * @throws PoolableException
	 */
	D getSource() throws PoolableException;

	/**
	 * return a Source to pool
	 * 
	 * @param d
	 * @throws PoolableException
	 */
	void close(D d) throws PoolableException;

	/**
	 * close pool
	 * 
	 * @throws PoolableException
	 */
	void close() throws PoolableException;
}
