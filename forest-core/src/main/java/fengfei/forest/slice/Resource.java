package fengfei.forest.slice;

import java.util.HashMap;
import java.util.Map;

public class Resource {

	protected String name;
	protected int weight = 1;
	protected Status status = Status.Normal;

	protected Map<String, String> extraInfo = new HashMap<>();

	public Resource(String name) {
		super();
		this.name = name;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		return true;
	}

	@Override
	public String toString() {
		return "Resource [name=" + name + ", weight=" + weight + ", status="
				+ status + ", extraInfo="
				+ extraInfo + "]";
	}

}
