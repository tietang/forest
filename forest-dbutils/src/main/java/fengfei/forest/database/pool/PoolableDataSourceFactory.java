package fengfei.forest.database.pool;

import java.util.Map;

import javax.sql.DataSource;

public interface PoolableDataSourceFactory {

	DataSource createDataSource(String driverClass, String url, String user,
			String password, Map<String, String> params)
			throws PoolableException;

	public void destory(DataSource dataSource) throws PoolableException;
}
