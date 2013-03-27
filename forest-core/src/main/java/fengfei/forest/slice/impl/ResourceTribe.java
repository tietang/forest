package fengfei.forest.slice.impl;

import java.util.ArrayList;
import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Resource;

public class ResourceTribe {

	protected List<Resource> availableResources = new ArrayList<>();
	protected List<Resource> failResources = new ArrayList<>();
	protected Plotter plotter = new HashPlotter();

	public ResourceTribe() {
	}

	public ResourceTribe(Plotter plotter) {
		super();
		this.plotter = plotter;
	}

	public void setPlotter(Plotter plotter) {
		this.plotter = plotter;
	}

	public List<Resource> getAvailableResources() {
		return availableResources;
	}

	public void setAvailableResources(List<Resource> availableResources) {
		this.availableResources = availableResources;
	}

	public void setFailResources(List<Resource> failResources) {
		this.failResources = failResources;
	}

	public void addResource(Resource resource) {
		availableResources.add(resource);
		// System.out.println("model: " + Resource);
		// System.out.println("model Resources: " + Resources);
	}

	public void removeResource(Resource resource) {
		availableResources.remove(resource);
		failResources.remove(resource);
		// System.out.println("model: " + Resource);
		// System.out.println("model Resources: " + Resources);
	}

	public List<Resource> getFailResources() {
		return failResources;
	}

	public Resource next(long seed) {
		int index = plotter.to(seed, availableResources, failResources);
		return availableResources.get(Math.abs(index));
	}

	@Override
	public String toString() {
		return "ResourceTribe [availableResources=" + availableResources
				+ ", failResources=" + failResources + ", plotter=" + plotter
				+ "]";
	}
}
