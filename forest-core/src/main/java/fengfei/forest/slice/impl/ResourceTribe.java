package fengfei.forest.slice.impl;

import java.util.ArrayList;
import java.util.List;

import fengfei.forest.slice.SliceResource;

public class ResourceTribe {

	protected List<SliceResource> availableResources = new ArrayList<>();
	protected List<SliceResource> failResources = new ArrayList<>();

	public ResourceTribe() {
	}

	public List<SliceResource> getAvailableResources() {
		return availableResources;
	}

	public void setAvailableResources(List<SliceResource> availableResources) {
		this.availableResources = availableResources;
	}

	public void setFailResources(List<SliceResource> failResources) {
		this.failResources = failResources;
	}

	public void addResource(SliceResource resource) {
		availableResources.add(resource);
		// //System.out.println("model: " + Resource);
		// //System.out.println("model Resources: " + Resources);
	}

	public void removeResource(SliceResource resource) {
		availableResources.remove(resource);
		failResources.remove(resource);
		// //System.out.println("model: " + Resource);
		// //System.out.println("model Resources: " + Resources);
	}

	public List<SliceResource> getFailResources() {
		return failResources;
	}

	@Override
	public String toString() {
		return "ResourceTribe [availableResources=" + availableResources
				+ ", failResources=" + failResources + " ]";
	}
}
