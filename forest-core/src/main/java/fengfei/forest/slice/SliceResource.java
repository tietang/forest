package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public class Resource {

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
	protected String id;
	protected String name;
	protected String schema = "0";
	protected int weight = 0;
	protected Status status = Status.Normal;
	protected Function function = Function.ReadWrite;
	protected Map<String, String> extraInfo = new HashMap<>();

	public Resource(String name) {
		super();
		this.name = name;
		createId();
	}

	public Resource(String name, String schema) {
		this(name);
		this.schema = schema;
		createId();
	}
	

	public Resource(String name, Function function) {
		this(name);
		this.function = function;
	}

	public Resource(String name, String schema, Function function) {
		this(name);
		this.schema = schema;
		this.function = function;
	}

	private void createId() {
		this.id = name + ":" + schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		createId();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
		createId();
	}

	public String getId() {
		return id;
	}

	public Long getSliceId() {
		return sliceId;
	}

	public void setSliceId(Long sliceId) {
		this.sliceId = sliceId;
	}

	public void addExtraInfo(String key, String value) {
		extraInfo.put(key, value);
	}

	public Map<String, String> getExtraInfo() {
		return extraInfo;
	}

	public void addExtraInfo(Map<String, String> extraInfo) {
		if (extraInfo == null) {
			return;
		}
		this.extraInfo.putAll(new HashMap<>(extraInfo));
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Resource [groupId=" + sliceId + ", id=" + id + ", weight="
				+ weight + ", status=" + status + ", function=" + function
				+ ", extraInfo=" + extraInfo + "]";
	}
}
