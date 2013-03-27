package fengfei.forest.slice.exception;

public class SliceException extends Exception {
	private static final long serialVersionUID = 1L;

	public SliceException() {
	}

	public SliceException(String description) {
		super(description);

	}

	public SliceException(String description, Throwable throwable) {
		super(description, throwable);

	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;
	}
}
