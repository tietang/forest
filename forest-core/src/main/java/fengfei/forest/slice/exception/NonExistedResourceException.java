package fengfei.forest.slice.exception;

public class NonExistedResourceException extends SliceRuntimeException {

	private static final long serialVersionUID = 1L;

	public NonExistedResourceException() {
		super();
	}

	public NonExistedResourceException(String description) {
		super(description);

	}

	public NonExistedResourceException(String description, Throwable throwable) {
		super(description, throwable);

	}
}
