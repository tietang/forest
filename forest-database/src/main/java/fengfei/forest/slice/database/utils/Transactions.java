package fengfei.forest.slice.database.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransactionRollbackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import fengfei.forest.database.DataAccessException;
import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.impl.DefaultForestGrower;
import fengfei.forest.database.pool.PoolableException;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.database.DatabaseRouterFactory;
import fengfei.forest.slice.database.PoolableDatabaseResource;
import fengfei.forest.slice.database.PoolableDatabaseRouter;
import fengfei.forest.slice.database.utils.MutiTransaction.MutiTaCallback;
import fengfei.forest.slice.exception.SliceException;

public class Transactions {

    static Map<String, PoolableDatabaseRouter<?>> poolableRouterCache = new HashMap<>();
    private static int retries = 3;
    public static DatabaseRouterFactory databaseRouterFactory = null;

    public static void setRetries(int retries) {
        Transactions.retries = retries;
    }

    public static void addRouter(String unitName, PoolableDatabaseRouter<?> router) {
        poolableRouterCache.put(unitName, router);
    }

    public static void setDatabaseSliceGroupFactory(DatabaseRouterFactory databaseRouterFactory) {
        setDatabaseSliceGroupFactory(databaseRouterFactory, false);

    }

    public static void setDatabaseSliceGroupFactory(
        DatabaseRouterFactory databaseRouterFactory,
        boolean isDev) {
        if (Transactions.databaseRouterFactory == null || isDev) {
            if (Transactions.databaseRouterFactory != null) {
                Map<String, PoolableDatabaseRouter<?>> routers =
                    Transactions.databaseRouterFactory.allPoolableRouters();
                Set<Entry<String, PoolableDatabaseRouter<?>>> sets = routers.entrySet();
                for (Entry<String, PoolableDatabaseRouter<?>> entry : sets) {
                    PoolableDatabaseRouter<?> router = entry.getValue();
                    try {
                        releaseDataSource(router);
                    } catch (PoolableException e) {
                        e.printStackTrace();
                    }
                }

            }
            Transactions.databaseRouterFactory = databaseRouterFactory;
        } else {
            throw new IllegalArgumentException(
                "DatabaseRouterFactory is already seted up, don't repeat configuration.");
        }

    }

    private static void releaseDataSource(PoolableDatabaseRouter<?> router)
        throws PoolableException {
        Map<String, DataSource> dses = router.allPooledDataSources();
        Set<Entry<String, DataSource>> sets = dses.entrySet();
        for (Entry<String, DataSource> entry : sets) {
            router.getPoolableDataSourceFactory().destory(entry.getValue());
        }

    }

    private static void check() throws DataAccessException {
        if (databaseRouterFactory == null) {
            throw new DataAccessException("DatabaseRouterFactory don't configed.");
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

    public static <T, E> T execute(
        String unitName,
        E key,
        Function function,
        TransactionCallback<T> callback) throws DataAccessException {
        try {
            PoolableDatabaseResource resource = get(unitName, key, function);
            return execute(resource, key, function, callback);
        } catch (SliceException | SQLException e) {
            throw new DataAccessException(String.format(
                "execute transaction error for unitName \"%s\" and key %s.",
                unitName,
                key.toString()), e);
        }
    }

    public static <T, E> T execute(String unitName, E key, TransactionCallback<T> callback)
        throws DataAccessException {
        try {
            PoolableDatabaseResource resource = get(unitName, key);
            return execute(resource, key, callback);
        } catch (SliceException | SQLException e) {
            throw new DataAccessException(String.format(
                "execute transaction error for unitName %s and key %s.",
                unitName,
                key.toString()));
        }
    }

    public static <T, E> T execute(
        PoolableDatabaseRouter<E> router,
        E key,
        Function function,
        TransactionCallback<T> callback) throws DataAccessException {

        try {
            PoolableDatabaseResource resource = router.locate(key, function);
            return execute(resource, key, function, callback);
        } catch (Exception e) {
            throw new DataAccessException(String.format(
                "execute transaction error for key %s.",
                key.toString()));
        }
    }

    public static <T, E> T execute(
        PoolableDatabaseRouter<E> router,
        E key,
        TransactionCallback<T> callback) throws DataAccessException {

        try {
            PoolableDatabaseResource resource = router.locate(key);
            return execute(resource, key, callback);
        } catch (Exception e) {
            throw new DataAccessException(String.format(
                "execute transaction error for key %s.",
                key.toString()));
        }
    }

    public static <T, E> T execute(
        PoolableDatabaseResource resource,
        E key,
        Function function,
        TransactionCallback<T> callback) throws SQLException {
        TaModel<T> model = null;
        try {
            model = new TaModel<>();
            model = retryExecute(model, resource, callback, retries);
            return model.returnValue;
            // } catch (SQLException e) {
            // if (model != null && model.grower != null) {
            // model.grower.rollback();
            // }
            //
            // throw e;
        } catch (Throwable e) {
            if (model != null && model.grower != null) {
                model.grower.rollback();
            }
            throw e;
        } finally {
            if (model != null && model.grower != null) {
                model.grower.close();
            }

        }

    }

    public static <T, E> T execute(
        PoolableDatabaseResource resource,
        E key,
        TransactionCallback<T> callback) throws SQLException {
        TaModel<T> model = null;
        try {

            model = new TaModel<>();
            model = retryExecute(model, resource, callback, retries);
            return model.returnValue;
            // } catch (SQLException e) {
            // if (model != null && model.grower != null) {
            // model.grower.rollback();
            // }
            //
            // throw e;
        } catch (Throwable e) {
            if (model != null && model.grower != null) {
                model.grower.rollback();
            }

            throw e;
        } finally {
            if (model != null && model.grower != null) {
                model.grower.close();
            }

        }

    }

    protected static <T> TaModel<T> retryExecute(
        TaModel<T> model,
        PoolableDatabaseResource resource,
        final TransactionCallback<T> callback,
        int tries) throws SQLException {
        T t = null;
        Connection connection = resource.getConnection();
        ForestGrower grower = new DefaultForestGrower(connection);
        model.grower = grower;
        try {
            grower.begin();
            t = callback.execute(grower, resource);
            connection.commit();

            model.returnValue = t;
            return model;
        } catch (SQLException e) {
            // re try write
            Throwable e2 = e.getCause();
            boolean isReTry =
                (e2 != null && (e2 instanceof SQLIntegrityConstraintViolationException
                // Duplicate entry for key 'PRIMARY'
                    || e2 instanceof SQLTransactionRollbackException
                    // for Deadlock found when trying to get lock; try
                    ))
                    || e instanceof SQLIntegrityConstraintViolationException
                    // for Deadlock found when trying to get lock; try
                    // restarting transaction
                    || e instanceof SQLTransactionRollbackException;

            if (tries > 0 && isReTry) {
                // GlobalStats.incr("retry times");
                grower.rollback();
                grower.commit();
                return retryExecute(model, resource, callback, tries - 1);
            } else {

                throw e;
            }
        }
    }

    protected static <T> TaModel<T> retryExecute(
        TaModel<T> model,
        DataSource dataSource,
        final JdbcCallback<T> callback,
        int tries) throws SQLException {
        T t = null;
        Connection connection = dataSource.getConnection();
        ForestGrower grower = new DefaultForestGrower(connection);
        model.grower = grower;
        try {
            grower.begin();
            t = callback.execute(grower);
            connection.commit();

            model.returnValue = t;
            return model;
        } catch (SQLException e) {
            // re try write
            Throwable e2 = e.getCause();
            boolean isReTry =
                (e2 != null && (e2 instanceof SQLIntegrityConstraintViolationException
                // Duplicate entry for key 'PRIMARY'
                    || e2 instanceof SQLTransactionRollbackException
                    // for Deadlock found when trying to get lock; try
                    ))
                    || e instanceof SQLIntegrityConstraintViolationException
                    // for Deadlock found when trying to get lock; try
                    // restarting transaction
                    || e instanceof SQLTransactionRollbackException;

            if (tries > 0 && isReTry) {
                // GlobalStats.incr("retry times");
                grower.rollback();
                grower.commit();
                return retryExecute(model, dataSource, callback, tries - 1);
            } else {

                throw e;
            }
        }
    }

    public static <T, E> T execute(DataSource dataSource, JdbcCallback<T> callback)
        throws SQLException {

        TaModel<T> model = null;
        try {

            model = new TaModel<>();
            model = retryExecute(model, dataSource, callback, retries);
            return model.returnValue;
            // } catch (SQLException e) {
            // if (model != null && model.grower != null) {
            // model.grower.rollback();
            // }
            //
            // throw e;
        } catch (Throwable e) {
            if (model != null && model.grower != null) {
                model.grower.rollback();
            }
            throw e;
        } finally {
            if (model != null && model.grower != null) {
                model.grower.close();
            }

        }

    }

    public static <T> PoolableDatabaseResource get(String unitName, T id) throws SliceException {

        @SuppressWarnings("unchecked")
        PoolableDatabaseRouter<T> router =
            (PoolableDatabaseRouter<T>) poolableRouterCache.get(unitName);
        if (router == null && databaseRouterFactory != null) {
            router = databaseRouterFactory.getPoolableRouter(unitName);
        }

        PoolableDatabaseResource resource = router.locate(id);
        return resource;
    }

    public static <T> PoolableDatabaseResource get(String unitName, T id, Function function)
        throws SliceException {
        @SuppressWarnings("unchecked")
        PoolableDatabaseRouter<T> router =
            (PoolableDatabaseRouter<T>) poolableRouterCache.get(unitName);
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

        T execute(ForestGrower grower, PoolableDatabaseResource resource) throws SQLException;
    }

    public static abstract class TaCallback<T> implements TransactionCallback<T> {

        public abstract T execute(ForestGrower grower, String suffix) throws SQLException;

        public T execute(ForestGrower grower, PoolableDatabaseResource resource)
            throws SQLException {
            String suffix = resource.getAlias();
            return execute(grower, suffix);

        }
    }

    public static class TaModel<T> {

        public ForestGrower grower;
        public T returnValue;

    }

}
