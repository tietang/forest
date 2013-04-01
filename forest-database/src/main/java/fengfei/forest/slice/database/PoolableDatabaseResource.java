package fengfei.forest.slice.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.exception.SliceException;

public class PoolableDatabaseResource extends DatabaseResource {
	DataSource dataSource;

	public PoolableDatabaseResource(SliceResource resource) {
		super(resource);
	}

	public PoolableDatabaseResource(SliceResource resource,
			DataSource dataSource) {
		super(resource);

		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		return dataSource.getConnection(username, password);
	}

}
