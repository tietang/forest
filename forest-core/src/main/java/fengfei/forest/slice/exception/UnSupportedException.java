package fengfei.forest.slice.exception;

public class UnSupportedException extends SliceRuntimeException {

	private static final long serialVersionUID = 1L;

	public UnSupportedException() {
		super();
	}

	public UnSupportedException(String description) {
		super(description);

	}

	public UnSupportedException(String description, Throwable throwable) {
		super(description, throwable);

	}

}
