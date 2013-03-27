package fengfei.forest.slice.exception;

public class NonExistedSliceException extends SliceRuntimeException {

	private static final long serialVersionUID = 1L;

	public NonExistedSliceException() {
		super();
	}

	public NonExistedSliceException(String description) {
		super(description);

	}

	public NonExistedSliceException(String description, Throwable throwable) {
		super(description, throwable);

	}
}
