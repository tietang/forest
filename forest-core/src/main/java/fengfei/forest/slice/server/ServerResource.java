package fengfei.forest.slice.server;

import fengfei.forest.slice.SliceResource;

public class ServerResource extends SliceResource {

	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";
	public static final String KEY_USER = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_SCHEMA = "schema";

	public ServerResource(SliceResource resource) {
		super(resource.getSliceId(), resource.getFunction(), resource.getResource());
	}

	public String getUsername() {
		return getExtraInfo().get(KEY_USER);
	}

	public void setUsername(String name) {
		getExtraInfo().put(KEY_USER, name);
	}

	public String getPassword() {
		return getExtraInfo().get(KEY_PASSWORD);
	}

	public void setPassword(String password) {
		getExtraInfo().put(KEY_PASSWORD, password);
	}

	public String getHost() {
		return getExtraInfo().get(KEY_HOST);
	}

	public String setHost(String host) {
		return getExtraInfo().put(KEY_HOST, host);
	}

	public int getPort() {
		return Integer.parseInt(getExtraInfo().get(KEY_PORT));
	}

	public int setPort(int port) {
		return setPort(String.valueOf(port));
	}

	public int setPort(String port) {
		return Integer.parseInt(getExtraInfo().put(KEY_PORT, port));
	}

	public String getSchema() {
		return getExtraInfo().get(KEY_SCHEMA);
	}

	public String setSchema(String schema) {
		return getExtraInfo().put(KEY_SCHEMA, schema);
	}

	@Override
	public String toString() {
		return "ServerResource [sliceId=" + sliceId + ", alias=" + alias + ", function=" + function + ", resource=" + resource + "]";
	}
}
