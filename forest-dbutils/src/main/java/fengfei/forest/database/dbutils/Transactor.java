package fengfei.forest.database.dbutils;

import java.sql.Connection;
import java.sql.SQLException;


public interface Transactor<T> {

	void begin() throws SQLException;

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void close() throws SQLException;

	Transactor<T> setConnection(Connection connection);

	Transactor<T> setCallback(TransactorCallback<T> callback);

	Transactor<T> execute() throws SQLException;

	T execute(TransactorCallback<T> callback) throws SQLException;

	T getResult();

	public static interface TransactorCallback<T> {

		T execute(ForestGrower grower) throws SQLException;
	}
}