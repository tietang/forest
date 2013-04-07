package fengfei.forest.slice.exception;

public class ConfigException extends SliceRuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigException() {
		super();
	}

	public ConfigException(String description) {
		super(description);

	}

	public ConfigException(String description, Throwable throwable) {
		super(description, throwable);

	}
}
