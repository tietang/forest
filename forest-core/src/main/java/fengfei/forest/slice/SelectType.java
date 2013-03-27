package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public enum SelectType {
	Hash(0),
	Remainder(1),
	Loop(2);

	private final int value;
	private static Map<Integer, SelectType> cache = new HashMap<Integer, SelectType>();
	static {
		for (SelectType type : values()) {
			cache.put(type.value, type);
		}
	}

	private SelectType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SelectType valueOf(int value) {
		return cache.get(value);
	}

	public static SelectType find(String name) {
		if (name == null || "".equals(name)) {
			return null;
		}
		SelectType[] fs = values();
		for (SelectType enumType : fs) {
			if (enumType.name().equalsIgnoreCase(name)) {
				return enumType;
			}

		}
		throw new IllegalArgumentException("Non-exist the enum type,error arg name:" + name);
	}
}
