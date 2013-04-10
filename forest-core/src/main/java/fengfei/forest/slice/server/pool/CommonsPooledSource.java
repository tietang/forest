package fengfei.forest.slice.server.pool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class CommonsPooledSource<D> implements PooledSource<D> {

	private ObjectPool<D> pool;

	public CommonsPooledSource(PoolableObjectFactory<D> poolableObjectFactory,
			GenericObjectPool.Config config) {
		super();
		pool = new GenericObjectPool<>(poolableObjectFactory, config);
	}

	@Override
	public D getSource() throws PoolableException {
		try {
			return pool.borrowObject();
		} catch (Exception e) {
			throw new PoolableException("can't borrow object ", e);
		}
	}

	@Override
	public void close(D d) throws PoolableException {
		try {
			pool.returnObject(d);
		} catch (Exception e) {
			throw new PoolableException("can't return object ", e);
		}
	}

	@Override
	public void close() throws PoolableException {
		try {
			pool.close();
			pool.clear();
		} catch (Exception e) {
			throw new PoolableException("close pool error ", e);
		}
	}
}
