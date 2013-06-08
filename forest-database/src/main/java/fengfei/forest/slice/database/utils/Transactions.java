package fengfei.forest.slice.database.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.vendor.NullExceptionSorter;

import fengfei.forest.database.DataAccessException;
import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.impl.DefaultForestGrower;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.database.DatabaseRouterFactory;
import fengfei.forest.slice.database.PoolableDatabaseResource;
import fengfei.forest.slice.database.PoolableDatabaseRouter;
import fengfei.forest.slice.exception.SliceException;

public class Transactions {

	static Map<String, PoolableDatabaseRouter<?>> poolableRouterCache = new HashMap<>();

	public static DatabaseRouterFactory databaseRouterFactory = null;

	public static void addRouter(String unitName,
			PoolableDatabaseRouter<?> router) {
		poolableRouterCache.put(unitName, router);
	}

	public static void setDatabaseSliceGroupFactory(
			DatabaseRouterFactory databaseRouterFactory) {
		if (Transactions.databaseRouterFactory == null) {
			Transactions.databaseRouterFactory = databaseRouterFactory;
		} else {
			throw new IllegalArgumentException(
					"DatabaseRouterFactory is already seted up, don't repeat configuration.");
		}

	}

	private static void check() throws DataAccessException {
		if (databaseRouterFactory == null) {
			throw new DataAccessException(
					"DatabaseRouterFactory don't configed.");
		}
	}

	public static void config(Config config) {
		if (databaseRouterFactory == null) {
			databaseRouterFactory = new DatabaseRouterFactory(config);
		} else {
			throw new IllegalArgumentException(
					"DatabaseRouterFactory have configed, don't repeat configuration.");
		}
	}

	public static <T, E> T execute(String unitName, E key, Function function,
			TransactionCallback<T> callback) throws DataAccessException {
		try {
			PoolableDatabaseResource resource = get(unitName, key, function);
			return execute(resource, key, function, callback);
		} catch (SliceException | SQLException e) {
			throw new DataAccessException(
					String.format(
							"execute transaction error for unitName \"%s\" and key %s.",
							unitName, key.toString()), e);
		}
	}

	public static <T, E> T execute(String unitName, E key,
			TransactionCallback<T> callback) throws DataAccessException {
		try {
			PoolableDatabaseResource resource = get(unitName, key);
			return execute(resource, key, callback);
		} catch (SliceException | SQLException e) {
			throw new DataAccessException(String.format(
					"execute transaction error for unitName %s and key %s.",
					unitName, key.toString()));
		}
	}

	public static <T, E> T execute(PoolableDatabaseRouter<E> router, E key,
			Function function, TransactionCallback<T> callback)
			throws DataAccessException {

		try {
			PoolableDatabaseResource resource = router.locate(key, function);
			return execute(resource, key, function, callback);
		} catch (Exception e) {
			throw new DataAccessException(String.format(
					"execute transaction error for key %s.", key.toString()));
		}
	}

	public static <T, E> T execute(PoolableDatabaseRouter<E> router, E key,
			TransactionCallback<T> callback) throws DataAccessException {

		try {
			PoolableDatabaseResource resource = router.locate(key);
			return execute(resource, key, callback);
		} catch (Exception e) {
			throw new DataAccessException(String.format(
					"execute transaction error for key %s.", key.toString()));
		}
	}

	public static <T, E> T execute(PoolableDatabaseResource resource, E key,
			Function function, TransactionCallback<T> callback)
			throws SQLException {

		Connection connection = resource.getConnection();
		// boolean isReadOnly = connection.getAutoCommit();
		connection.setAutoCommit(false);
		ForestGrower grower = new DefaultForestGrower(connection);
		try {
			T t = callback.execute(grower, resource);
			connection.commit();
			return t;
		} catch (SQLException e) {
			grower.rollback();
			throw e;
		} catch (Throwable e) {
			throw e;
		} finally {
			grower.close();
		}

	}

	public static <T, E> T execute(PoolableDatabaseResource resource, E key,
			TransactionCallback<T> callback) throws SQLException {

		Connection connection = resource.getConnection();
		// boolean isReadOnly = connection.getAutoCommit();
		connection.setAutoCommit(false);
		ForestGrower grower = new DefaultForestGrower(connection);
		try {
			T t = callback.execute(grower, resource);
			connection.commit();
			return t;
		} catch (SQLException e) {
			grower.rollback();
			throw e;
		} catch (Throwable e) {
			throw e;
		} finally {
			grower.close();
		}

	}

	public static <T, E> T execute(DataSource dataSource,
			JdbcCallback<T> callback) throws SQLException {

		Connection connection = dataSource.getConnection();
		// boolean isReadOnly = connection.getAutoCommit();
		connection.setAutoCommit(false);
		ForestGrower grower = new DefaultForestGrower(connection);
		try {
			T t = callback.execute(grower);
			connection.commit();
			return t;
		} catch (SQLException e) {
			grower.rollback();
			throw e;
		} catch (Throwable e) {
			throw e;
		} finally {
			grower.close();
		}

	}

	public static <T> PoolableDatabaseResource get(String unitName, T id)
			throws SliceException {
	
		@SuppressWarnings("unchecked")
		PoolableDatabaseRouter<T> router = (PoolableDatabaseRouter<T>) poolableRouterCache
				.get(unitName);
		if (router == null && databaseRouterFactory != null) {
			router = databaseRouterFactory.getPoolableRouter(unitName);
		}

		PoolableDatabaseResource resource = router.locate(id);
		return resource;
	}

	public static <T> PoolableDatabaseResource get(String unitName, T id,
			Function function) throws SliceException {
		@SuppressWarnings("unchecked")
		PoolableDatabaseRouter<T> router = (PoolableDatabaseRouter<T>) poolableRouterCache
				.get(unitName);
		if (router == null && databaseRouterFactory != null) {
			router = databaseRouterFactory.getPoolableRouter(unitName);
		}
		PoolableDatabaseResource resource = router.locate(id, function);
		return resource;
	}

	public static interface JdbcCallback<T> {

		T execute(ForestGrower grower) throws SQLException;
	}

	public static interface TransactionCallback<T> {

		T execute(ForestGrower grower, PoolableDatabaseResource resource)
				throws SQLException;
	}

	public static abstract class TaCallback<T> implements
			TransactionCallback<T> {

		public abstract T execute(ForestGrower grower, String suffix)
				throws SQLException;

		public T execute(ForestGrower grower, PoolableDatabaseResource resource)
				throws SQLException {
			String suffix = resource.getAlias();
			return execute(grower, suffix);

		}
	}
}
