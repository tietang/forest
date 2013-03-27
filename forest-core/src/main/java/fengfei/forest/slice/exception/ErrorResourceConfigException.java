package fengfei.forest.slice.exception;

public class ErrorResourceConfigException extends SliceRuntimeException {

	private static final long serialVersionUID = 1L;

	public ErrorResourceConfigException() {
		super();
	}

	public ErrorResourceConfigException(String description) {
		super(description);

	}

	public ErrorResourceConfigException(String description, Throwable throwable) {
		super(description, throwable);

	}
}
