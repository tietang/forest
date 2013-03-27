package fengfei.forest.slice.exception;

public class SliceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SliceRuntimeException() {
		super();
	}

	public SliceRuntimeException(String description) {
		super(description);

	}

	public SliceRuntimeException(String description, Throwable throwable) {
		super(description, throwable);

	}

//	@Override
//	public synchronized Throwable fillInStackTrace() {
//		return null;
//	}
}
