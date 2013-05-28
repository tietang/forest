package fengfei.forest.slice.exception;

public class NonExistedSliceConfigException extends ConfigException {

	private static final long serialVersionUID = 1L;

	public NonExistedSliceConfigException() {
		super();
	}

	public NonExistedSliceConfigException(String description) {
		super(description);

	}

	public NonExistedSliceConfigException(String description, Throwable throwable) {
		super(description, throwable);

	}
}
