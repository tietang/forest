package fengfei.forest.slice.config;

import java.util.HashMap;
import java.util.Map;

public enum RouterType {
        Navigable(10),
        FixedLengthNavigable(11),
        Accuracy(20);

    private final int value;
    private static Map<Integer, RouterType> cache = new HashMap<Integer, RouterType>();
    static {
        for (RouterType sliceType : values()) {
            cache.put(sliceType.value, sliceType);
        }
    }

    private RouterType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RouterType valueOf(int value) {
        return cache.get(value);
    }

    public static RouterType find(String name) {
        if (name == null || "".equals(name.trim())) {
            return null;
        }
        RouterType[] fs = values();
        for (RouterType enumType : fs) {
            if (enumType.name().equalsIgnoreCase(name)) {
                return enumType;
            }

        }
        throw new IllegalArgumentException("Non-exist the enum type,error arg name:" + name);
    }

}
