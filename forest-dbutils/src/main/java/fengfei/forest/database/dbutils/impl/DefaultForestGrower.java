package fengfei.forest.database.dbutils.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.ListHandler;
import fengfei.forest.database.dbutils.OneBeanHandler;
import fengfei.forest.database.dbutils.SingleValueHandler;
import fengfei.forest.database.dbutils.Transducer;
import fengfei.forest.database.dbutils.impl.ForestRunner.InsertResultSet;

public class DefaultForestGrower implements ForestGrower {

	private static Logger logger = LoggerFactory
			.getLogger(DefaultForestGrower.class);
	private Connection connection;
	private ForestRunner runner;

	public DefaultForestGrower(Connection connection) {
		runner = new ForestRunner();
		setConnection(connection);
	}

	@Override
	public <T> List<T> select(String sql, Transducer<T> transducer,
			Object... params) throws SQLException {
		printlnSQL(sql, params);
		List<T> list = runner.query(connection, sql, new ListHandler<T>(
				transducer), params);
		return list;
	}

	@Override
	public <T> List<T> select(String sql, Class<T> clazz, Object... params)
			throws SQLException {
		printlnSQL(sql, params);
		List<T> list = runner.query(connection, sql, new BeanListHandler<T>(
				clazz), params);
		return list;
	}

	public List<Map<String, Object>> select(String sql, Object... params)
			throws SQLException {
		printlnSQL(sql, params);
		List<Map<String, Object>> list = runner.query(connection, sql,
				new MapListHandler(), params);
		return list;
	}

	@Override
	public <T> T selectOne(String sql, Transducer<T> transducer,
			Object... params) throws SQLException {
		printlnSQL(sql, params);
		T one = runner.query(connection, sql,
				new OneBeanHandler<T>(transducer), params);
		return one;
	}

	@Override
	public <T> T selectOne(String sql, Class<T> clazz, Object... params)
			throws SQLException {
		printlnSQL(sql, params);
		T one = runner
				.query(connection, sql, new BeanHandler<T>(clazz), params);
		return one;
	}

	public Map<String, Object> selectOne(String sql, Object... params)
			throws SQLException {
		printlnSQL(sql, params);
		Map<String, Object> one = runner.query(connection, sql,
				new MapHandler(), params);
		return one;
	}

	@Override
	public int count(String sql, Object... params) throws SQLException {
		printlnSQL(sql, params);
		String one = runner.query(connection, sql, new SingleValueHandler(),
				params);
		if (one == null) {
			return 0;
		} else {
			int ct = Integer.parseInt(one);
			return ct;
		}
	}

	@Override
	public int update(String sql, Object... params) throws SQLException {
		printlnSQL(sql, params);

		boolean isReadOnly = connection.isReadOnly();
		connection.setReadOnly(false);
		int ct = runner.update(connection, sql, params);
		connection.setReadOnly(isReadOnly);
		return ct;
	}

	@Override
	public InsertResultSet<Long> insert(String sql, Object... params)
			throws SQLException {
		printlnSQL(sql, params);

		boolean isReadOnly = connection.isReadOnly();
		connection.setReadOnly(false);
		InsertResultSet<Long> irs = runner.insertForLong(connection, sql,
				params);
		connection.setReadOnly(isReadOnly);

		return irs;
	}

	@Override
	public InsertResultSet<Integer> insertForInt(String sql, Object... params)
			throws SQLException {
		printlnSQL(sql, params);

		boolean isReadOnly = connection.isReadOnly();
		connection.setReadOnly(false);
		InsertResultSet<Integer> irs = runner.insertForInt(connection, sql,
				params);
		connection.setReadOnly(isReadOnly);

		return irs;
	}

	@Override
	public int batchUpdate(String sql, Object[]... params) throws SQLException {
		boolean isReadOnly = connection.isReadOnly();
		connection.setReadOnly(false);
		runner.batch(connection, sql, params);
		connection.setReadOnly(isReadOnly);
		return 0;
	}

	@Override
	public void begin() throws SQLException {
		this.connection.setAutoCommit(false);
	}

	@Override
	public void commit() throws SQLException {
		if (connection != null) {
			this.connection.commit();
		}
	}

	@Override
	public void rollback() throws SQLException {
		if (connection != null) {
			this.connection.rollback();
		}
		// try {
		// connection.setAutoCommit(true);
		// } catch (SQLException e) {
		// log.warn("Can't setAutoCommit true", e);
		// }
	}

	private void printlnSQL(String sql, Object... params) {
		logger.debug(sql + "  params: "
				+ Arrays.asList(params == null ? new Object[] {} : params));
	}

	@Override
	public void close() throws SQLException {
		if (connection != null) {
			// try {
			// connection.setAutoCommit(true);
			// } catch (SQLException e) {
			// log.warn("Can't setAutoCommit true", e);
			// }
			connection.close();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
