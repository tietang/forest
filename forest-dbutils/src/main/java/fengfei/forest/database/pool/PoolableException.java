package fengfei.forest.database.pool;

public class PoolableException extends Exception {

	private static final long serialVersionUID = 1L;

	public PoolableException() {
	}

	public PoolableException(String description) {
		super(description);

	}

	public PoolableException(String description, Throwable throwable) {
		super(description, throwable);

	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
