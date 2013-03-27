package fengfei.forest.database.pool;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;

public class ClosablePoolingDataSource extends PoolingDataSource {
	public ClosablePoolingDataSource(ObjectPool<?> pool) {
		_pool = pool;
	}

	public void close() {
		if (this._pool != null) {
			try {
				_pool.close();
				_pool.clear();
			} catch (Exception e) {
			}
		}
	}
}
