package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public class SliceResource {
	public static enum Function {
		Read, Write, ReadWrite;

		public static Function find(String name) {
			if (name == null || "".equals(name)) {
				return null;
			}
			Function[] fs = values();
			for (Function enumType : fs) {
				if (enumType.name().equalsIgnoreCase(name)) {
					return enumType;
				}
			}
			throw new IllegalArgumentException(
					"Non-exist the enum type,error arg name:" + name);
		}
	}

	protected Long sliceId;
	protected String alias;
	protected Function function = Function.ReadWrite;
	protected Resource resource;
	protected Map<String, String> params = new HashMap<>();

	public SliceResource(Long sliceId, Function function, Resource resource) {
		this(sliceId, resource);
		this.function = function;
		this.alias = String.valueOf(sliceId);
	}

	public SliceResource(Long sliceId, Resource resource) {
		super();
		this.sliceId = sliceId;
		this.resource = resource;
		this.alias = String.valueOf(sliceId);
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void addParams(Map<String, String> params) {
		if (params == null) {
			return;
		}
		this.params.putAll(new HashMap<>(params));
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Long getSliceId() {
		return sliceId;
	}

	public void setSliceId(Long sliceId) {
		this.sliceId = sliceId;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getName() {
		return resource.getName();
	}

	public Map<String, String> getExtraInfo() {
		return resource.extraInfo;
	}

	public int getWeight() {
		return resource.weight;
	}

	public Status getStatus() {
		return resource.status;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@Override
	public String toString() {
		return "SliceResource [sliceId=" + sliceId + ", alias=" + alias
				+ ", function=" + function + ", resource=" + resource
				+ ", params=" + params + "]";
	}

}
