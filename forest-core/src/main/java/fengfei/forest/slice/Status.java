package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	Normal(0), Busy(1), Error(2), Cancelled(3);
	private final int value;
	private static Map<Integer, Status> cache = new HashMap<Integer, Status>();
	static {
		for (Status busy : values()) {
			cache.put(busy.value, busy);
		}
	}

	private Status(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Status valueOf(int value) {
		return cache.get(value);
	}
}
