package fengfei.forest.database.dbutils.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.QueryRunner;

public class ForestRunner extends QueryRunner {

	protected void rethrow(SQLException cause, String sql, Object... params)
			throws SQLException {

		String causeMessage = cause.getMessage();
		if (causeMessage == null) {
			causeMessage = "";
		}
		StringBuffer msg = new StringBuffer(causeMessage);

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Arrays.deepToString(params));
		}

		SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
				cause.getErrorCode(), cause);

		// e.setNextException(cause);

		throw e;
	}

	public InsertResultSet<Integer> insertForInt(String sql, Object... params)
			throws SQLException {
		Connection conn = this.prepareConnection();

		return this.insert(Integer.class, conn, true, sql, params);
	}

	public InsertResultSet<Integer> insertForInt(Connection conn, String sql,
			Object... params) throws SQLException {
		return this.insert(Integer.class, conn, true, sql, params);
	}

	public InsertResultSet<Long> insertForLong(Connection conn, String sql,
			Object... params) throws SQLException {
		return this.insert(Long.class, conn, false, sql, params);
	}

	public InsertResultSet<Long> insertForLong(String sql, Object... params)
			throws SQLException {
		Connection conn = this.prepareConnection();

		return this.insert(Long.class, conn, true, sql, params);
	}

	/**
	 * Calls update after checking the parameters to ensure nothing is null.
	 * 
	 * @param conn
	 *            The connection to use for the update call.
	 * @param closeConn
	 *            True if the connection should be closed, false otherwise.
	 * @param sql
	 *            The SQL statement to execute.
	 * @param params
	 *            An array of update replacement parameters. Each row in this
	 *            array is one set of update replacement values.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             If there are database or parameter errors.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Number> InsertResultSet<T> insert(Class<T> clazz,
			Connection conn, boolean closeConn, String sql, Object... params)
			throws SQLException {
		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			if (closeConn) {
				close(conn);
			}
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement stmt = null;
		int rows = 0;
		InsertResultSet<?> resultSet = null;
		try {
			stmt = this.prepareStatement(conn, sql);
			this.fillStatement(stmt, params);
			rows = stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();

			if (rs.next()) {

				if (clazz == Long.class) {
					long autoPk = rs.getLong(1);
					resultSet = new InsertResultSet<>(rows, autoPk);
				} else if (clazz == Integer.class) {
					int autoPk = rs.getInt(1);
					resultSet = new InsertResultSet<>(rows, autoPk);
				}

			} else {
				resultSet = new InsertResultSet<>(rows, null);
			}

		} catch (SQLException e) {
			this.rethrow(e, sql, params);

		} finally {
			close(stmt);
			if (closeConn) {
				close(conn);
			}
		}

		return (InsertResultSet<T>) resultSet;
	}

	public static class InsertResultSet<T extends Number> {
		public int rows;
		public T autoPk;

		public InsertResultSet(int rows, T autoPk) {
			super();
			this.rows = rows;
			this.autoPk = autoPk;
		}

	}
}
