package fengfei.forest.database.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import fengfei.forest.database.dbutils.impl.ForestRunner.InsertResultSet;

public interface ForestGrower {

	<T> List<T> select(String sql, Transducer<T> transducer, Object... params)
			throws SQLException;

	<T> List<T> select(String sql, Class<T> clazz, Object... params)
			throws SQLException;

	List<Map<String, Object>> select(String sql, Object... params)
			throws SQLException;

	<T> T selectOne(String sql, Transducer<T> transducer, Object... params)
			throws SQLException;

	<T> T selectOne(String sql, Class<T> clazz, Object... params)
			throws SQLException;

	Map<String, Object> selectOne(String sql, Object... params)
			throws SQLException;

	int count(String sql, Object... params) throws SQLException;

	int update(String sql, Object... params) throws SQLException;

	InsertResultSet<Long> insert(String sql, Object... params)
			throws SQLException;

	public InsertResultSet<Integer> insertForInt(String sql, Object... params)
			throws SQLException;

	int batchUpdate(String sql, Object[]... params) throws SQLException;

	void begin() throws SQLException;

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void close() throws SQLException;

	Connection getConnection();

	void setConnection(Connection connection);
}
