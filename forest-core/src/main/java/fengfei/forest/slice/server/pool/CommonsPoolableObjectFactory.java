package fengfei.forest.slice.server.pool;

import java.util.Map;

import org.apache.commons.pool.PoolableObjectFactory;

public abstract class CommonsPoolableObjectFactory<D> implements PoolableObjectFactory<D> {

	protected Map<String, String> params;
	protected String host;
	protected int port;
	protected String schema;
	protected String username;
	protected String password;

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
