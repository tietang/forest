package fengfei.forest.slice.database.utils;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.slice.database.utils.Transactions.JdbcCallback;

public class MutiTransaction<T> {

    private static final Logger logger = LoggerFactory.getLogger(MutiTransaction.class);
    protected int counter = 0;
    private static int retries = 3;
    private List<TransactionModel<T>> models = new ArrayList<>();

    public MutiTransaction() {
    }

    public void addTransactionModel(ForestGrower grower, String suffix, MutiTaCallback<T> callback) {
        models.add(new TransactionModel<>(grower, suffix, callback));
    }

    public void addTransactionModel(
        int index,
        ForestGrower grower,
        String suffix,
        MutiTaCallback<T> callback) {
        models.add(index, new TransactionModel<>(grower, suffix, callback));
    }

    public void addTransactionModel(TransactionModel<T> model) {
        models.add(model);
    }

    public void addTransactionModel(int index, TransactionModel<T> model) {
        models.add(index, model);
    }

    public static void setRetries(int retries) {
        MutiTransaction.retries = retries;
    }

    protected T retryExecute(int index, final TransactionModel<T> model, int tries)
        throws SQLException {
        T t = null;
        try {
            model.getGrower().begin();
            t = model.execute(index, model.getGrower());
            return t;
        } catch (SQLException e) {
            // re try write
            Throwable e2 = e.getCause();
            boolean isReTry = (e2 != null && (e2 instanceof SQLIntegrityConstraintViolationException
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
                model.getGrower().rollback();
                model.getGrower().commit();
                return retryExecute(index, model, tries - 1);
            } else {

                throw e;
            }
        }
    }

    public List<T> execute() throws SQLException {

        synchronized (this) {
            try {

                List<T> ts = new ArrayList<>();
//                for (final TransactionModel<T> model : models) {
//                    model.getGrower().begin();               
//                }
                for (final TransactionModel<T> model : models) {
                    // t = call(model);
                    T t = retryExecute(getCounter(), model, retries);
                    increment();
                    ts.add(t);
                }
                return ts;

            } catch (SQLException e) {
                rollback(models);
                throw e;
            } catch (Exception e) {
//                rollback(models);
                throw new SQLException(e.getMessage(), e);
            } finally {
                try {
                    commitAndClose(models);
                } catch (SQLException e) {
                    throw e;
                }
            }
        }

    }

    public int getCounter() {
        return counter;
    }

    public int increment() {
        return counter++;
    }

    protected void rollback(List<TransactionModel<T>> models) throws SQLException {
        try {
            for (int j = 0; j < models.size(); j++) {
                TransactionModel<T> model = models.get(j);
                if (model != null) {
                    ForestGrower grower = model.getGrower();
                    grower.rollback();
                }
            }
        } catch (SQLException e1) {
            throw new SQLException("rollback error," + e1.getMessage(), e1);
        }
    }

    protected void begin(List<TransactionModel<T>> models) throws SQLException {
        try {
            for (TransactionModel<T> model : models) {
                if (model != null) {
                    ForestGrower grower = model.getGrower();
                    grower.begin();
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    protected void commitAndClose(List<TransactionModel<T>> models) throws SQLException {
        List<Throwable> lastErrors = new ArrayList<Throwable>();
        for (TransactionModel<T> model : models) {
            if (model != null) {
                ForestGrower grower = model.getGrower();
                try {
                    grower.commit();
                } catch (Throwable e) {
                    lastErrors.add(e);
                    logger.error("commit error!", e);
                } finally {
                    try {
                        grower.close();
                    } catch (SQLException e) {
                        logger.error("close error!", e);
                    }
                }
            }
        }
        if (lastErrors.size() > 0)
            throw new SQLException(
                "commit error, error statck size: " + lastErrors.size(),
                lastErrors.get(0));
    }

    public static abstract class MutiTaCallback<T> implements JdbcCallback<T> {

        protected String suffix;
        protected int index;

        public T execute(int index, ForestGrower grower, String suffix) throws SQLException {
            this.index=index;
            this.suffix = suffix;
            return execute(grower);
        }

    }

    public static class TransactionModel<T> {

        protected ForestGrower grower;
        protected String suffix;
        protected MutiTaCallback<T> callback;

        public TransactionModel() {
        }

        public TransactionModel(ForestGrower grower, String suffix, MutiTaCallback<T> callback) {
            super();
            this.grower = grower;
            this.suffix = suffix;
            this.callback = callback;
        }

        public T execute(int index, ForestGrower grower) throws SQLException {
            return callback.execute(index, grower, suffix);
        }

        public MutiTaCallback<T> getCallback() {
            return callback;
        }

        public void setCallback(MutiTaCallback<T> callback) {
            this.callback = callback;
        }

        public ForestGrower getGrower() {
            return grower;
        }

        public void setGrower(ForestGrower grower) {
            this.grower = grower;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

    }

}