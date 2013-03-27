package fengfei.forest.database.dbutils.impl;

import java.sql.Connection;
import java.sql.SQLException;

import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.Transactor;

public class DefaultTransactor<T> implements Transactor<T> {

	private Connection connection;
	private ForestGrower grower;
	private TransactorCallback<T> callback;
	private T result;

	public DefaultTransactor(Connection connection) {
		this.connection = connection;
		this.grower = new DefaultForestGrower(connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#begin()
	 */
	@Override
	public void begin() throws SQLException {
		grower.begin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#commit()
	 */
	@Override
	public void commit() throws SQLException {
		grower.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#rollback()
	 */
	@Override
	public void rollback() throws SQLException {
		grower.rollback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#close()
	 */
	@Override
	public void close() throws SQLException {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fengfei.forest.database.dbutils.Transactor#setConnection(java.sql.Connection
	 * )
	 */
	@Override
	public Transactor<T> setConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fengfei.forest.database.dbutils.Transactor#setCallback(fengfei.forest
	 * .database.dbutils.Transaction.TransactionCallback)
	 */
	@Override
	public Transactor<T> setCallback(TransactorCallback<T> callback) {
		this.callback = callback;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#execute()
	 */
	@Override
	public Transactor<T> execute() throws SQLException {
		begin();
		result = callback.execute(this.grower);
		commit();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.forest.database.dbutils.Transactor#getResult()
	 */
	@Override
	public T getResult() {
		return result;
	}

	@Override
	public T execute(Transactor.TransactorCallback<T> callback)
			throws SQLException {
		begin();
		result = callback.execute(this.grower);
		commit();
		return result;
	}

}
