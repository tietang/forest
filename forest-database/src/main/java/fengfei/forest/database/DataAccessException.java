package fengfei.forest.database;

public class DataAccessException extends Exception {

	private static final long serialVersionUID = 3601527062523682638L;

	public DataAccessException() {
		super();
	}

	public DataAccessException(String msg) {
		super(msg);
	}

	public DataAccessException(String msg, Throwable e) {
		super(msg, e);
	}

}
