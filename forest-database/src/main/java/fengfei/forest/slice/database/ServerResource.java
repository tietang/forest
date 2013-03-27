package fengfei.forest.slice.database;

import fengfei.forest.slice.Resource;

public class ServerResource extends Resource {

	static final String KEY_HOST = "host";
	static final String KEY_PORT = "port";
	static final String KEY_USER = "username";
	static final String KEY_PASSWORD = "password";
	static final String KEY_DRIVER_CLASS = "driverClass";
	static final String KEY_SCHEMA = "schema";
	static final String KEY_DATABASE = "database";
	int port;
	String host;

	public ServerResource(Resource resource) {
		super(resource.getName(), resource.getSchema());
		this.status = resource.getStatus();
		this.extraInfo = resource.getExtraInfo();
		this.id = resource.getId();
		this.weight = resource.getWeight();
		splitName();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		splitName();
	}

	@Override
	public void setSchema(String schema) {
		super.setSchema(schema);
		splitName();
	}

	private void splitName() {
		String[] sr = name.split(":");
		if (sr.length >= 2) {
			this.host = sr[0];
			this.port = Integer.parseInt(sr[1]);

		} else {
			throw new IllegalArgumentException("resource's name is error.");
		}
	}

	public String getUsername() {
		return extraInfo.get(KEY_USER);
	}

	public String getPassword() {
		return extraInfo.get(KEY_PASSWORD);
	}

	public String getHost() {

		return host;// extraInfo.get(KEY_HOST);
	}

	public int getPort() {
		return port;// Integer.parseInt(extraInfo.get(KEY_PORT));
	}

	public String getDriverClass() {
		return extraInfo.get(KEY_DRIVER_CLASS);
	}

	public String getSchema() {
		return schema;// extraInfo.get(KEY_SCHEMA);
	}

	public String getDatabase() {
		return extraInfo.get(KEY_DATABASE);
	}

	public String getDatabaseName() {
		return extraInfo.get(KEY_DATABASE);
	}
}
