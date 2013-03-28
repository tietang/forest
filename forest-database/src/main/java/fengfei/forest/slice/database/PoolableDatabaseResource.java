package fengfei.forest.slice.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.exception.SliceException;

public class PoolableDatabaseResource extends DatabaseResource {
	DataSource dataSource;

	public PoolableDatabaseResource(SliceResource resource) {
		super(resource);
	}

	public PoolableDatabaseResource(SliceResource resource, DataSource dataSource) {
		super(resource);

		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SliceException {

		if (dataSource == null) {
			throw new SliceException("dataSource is null");
		} else {
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {

				throw new SliceException("Can't get connection for Resource "
						+ this, e);
			}
		}
	}

}
