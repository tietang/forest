package fengfei.forest.slice.database;

import fengfei.forest.slice.SliceResource;

public class ServerResource extends SliceResource {

	static final String KEY_HOST = "host";
	static final String KEY_PORT = "port";
	static final String KEY_USER = "username";
	static final String KEY_PASSWORD = "password";
	static final String KEY_DRIVER_CLASS = "driverClass";
	static final String KEY_SCHEMA = "schema";
	static final String KEY_DATABASE = "database";


	public ServerResource(SliceResource resource) {
		super(resource.getSliceId(), resource.getFunction(), resource.getResource());
	}

	public String getUsername() {
		return resource.getExtraInfo().get(KEY_USER);
	}

	public String getPassword() {
		return resource.getExtraInfo().get(KEY_PASSWORD);
	}

	public String getHost() {
		return resource.getExtraInfo().get(KEY_HOST);
	}

	public int getPort() {
		return Integer.parseInt(resource.getExtraInfo().get(KEY_PORT));
	}

	public String getDriverClass() {
		return resource.getExtraInfo().get(KEY_DRIVER_CLASS);
	}

	public String getSchema() {
		return resource.getExtraInfo().get(KEY_SCHEMA);
	}

	public String getDatabase() {
		return resource.getExtraInfo().get(KEY_DATABASE);
	}

	public String getDatabaseName() {
		return resource.getExtraInfo().get(KEY_DATABASE);
	}
}
