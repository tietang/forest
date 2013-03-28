package fengfei.forest.slice.database;

import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.server.ServerResource;

public class DatabaseResource extends ServerResource {

	public static final String KEY_DRIVER_CLASS = "driverClass";
	public static final String KEY_DATABASE = "database";

	public DatabaseResource(SliceResource resource) {
		super(resource);
	}

	public String getDriverClass() {
		return getExtraInfo().get(KEY_DRIVER_CLASS);
	}

	public String setDriverClass(String driverClass) {
		return getExtraInfo().put(KEY_DRIVER_CLASS, driverClass);
	}

	public String getDatabase() {
		return getExtraInfo().get(KEY_DATABASE);
	}

	public String setDatabase(String database) {
		return getExtraInfo().put(KEY_DATABASE, database);
	}
}
