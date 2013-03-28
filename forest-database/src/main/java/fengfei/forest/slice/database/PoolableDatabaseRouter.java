package fengfei.forest.slice.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import fengfei.forest.database.pool.PoolableDataSourceFactory;
import fengfei.forest.database.pool.PoolableException;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.server.ServerRouter;

public class PoolableDatabaseRouter<Key> extends ServerRouter<Key> {

	private Map<String, DataSource> pooledDataSources = new ConcurrentHashMap<>();
	private ConnectonUrlMaker urlMaker;
	private PoolableDataSourceFactory poolableDataSourceFactory;
	

	public PoolableDatabaseRouter(
			Router<Key> router,
			ConnectonUrlMaker urlMaker,
			PoolableDataSourceFactory poolableDataSourceFactory) {
		super(router);
		this.urlMaker = urlMaker;
		this.poolableDataSourceFactory = poolableDataSourceFactory;
	}

	public PoolableDatabaseResource locate(Key key) {
		PoolableDatabaseResource res = new PoolableDatabaseResource(router.locate(key));
		return locate(res);
	}

	public PoolableDatabaseResource locate(Key key, Function function) {
		PoolableDatabaseResource res = new PoolableDatabaseResource(router.locate(key, function));
		return locate(res);
	}

	private PoolableDatabaseResource locate(PoolableDatabaseResource res) {
		String url = urlMaker.makeUrl(res);
		DataSource dataSource = pooledDataSources.get(url);
		if (dataSource == null) {
			try {
				dataSource = poolableDataSourceFactory.createDataSource(
						res.getDriverClass(),
						url,
						res.getUsername(),
						res.getPassword(),
						res.getExtraInfo());
				pooledDataSources.put(url, dataSource);
			} catch (PoolableException e) {
				throw new SliceRuntimeException(
						"Can't create datasource for the slice " + res,
						e);
			}
		}
		if (dataSource == null) {
			throw new NonExistedSliceException("Can't get datasource for the slice" + res);
		}
		res.setDataSource(dataSource);
		return res;
	}

	//
	// public Connection getConnection(Source key) throws SliceException {
	// PoolableServerResource slice = get(key);
	// DataSource dataSource = getDataSource(slice);
	// if (dataSource == null) {
	// throw new SliceException("");
	// } else {
	// try {
	// return dataSource.getConnection();
	// } catch (SQLException e) {
	//
	// throw new SliceException("Can't get connection for slice "
	// + slice, e);
	// }
	// }
	// }
	//
	// public Connection getConnection(Source key, Function function)
	// throws SliceException {
	// PoolableServerResource slice = get(key, function);
	// DataSource dataSource = getDataSource(slice);
	// if (dataSource == null) {
	// throw new SliceException("");
	// } else {
	// try {
	// return dataSource.getConnection();
	// } catch (SQLException e) {
	// throw new SliceException(String.format(
	// "Can't get connection by Function(%s), for slice %s",
	// function.name(), slice), e);
	// }
	// }
	// }
	public Map<String, DataSource> allPooledDataSources() {
		return pooledDataSources;
	}

	public PoolableDataSourceFactory getPoolableDataSourceFactory() {
		return poolableDataSourceFactory;
	}

	@Override
	public String toString() {
		return "PoolableDatabaseRouter [urlMaker=" + urlMaker + ", poolableDataSourceFactory=" + poolableDataSourceFactory + ", router=" + router + "]";
	}

	@Override
	public PoolableDatabaseResource first() {
		return new PoolableDatabaseResource(router.first());
	}

	@Override
	public PoolableDatabaseResource first(Function function) {
		return new PoolableDatabaseResource(router.first(function));
	}

	@Override
	public PoolableDatabaseResource last() {
		return new PoolableDatabaseResource(router.last());
	}

	@Override
	public PoolableDatabaseResource last(Function function) {
		return new PoolableDatabaseResource(router.last(function));
	}
}
