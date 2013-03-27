package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public enum OverflowType {

	Last(0),
	First(10),
	New(20),
	Exception(30);

	private final int value;
	private static Map<Integer, OverflowType> cache = new HashMap<Integer, OverflowType>();
	static {
		for (OverflowType type : values()) {
			cache.put(type.value, type);
		}
	}

	private OverflowType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static OverflowType valueOf(int value) {
		return cache.get(value);
	}

	public static OverflowType find(String name) {
		if (name == null || "".equals(name)) {
			return null;
		}
		OverflowType[] fs = values();
		for (OverflowType enumType : fs) {
			if (enumType.name().equalsIgnoreCase(name)) {
				return enumType;
			}

		}
		throw new IllegalArgumentException("Non-exist the enum type,error arg name:" + name);
	}
}
