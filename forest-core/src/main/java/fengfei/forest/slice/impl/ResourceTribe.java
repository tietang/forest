package fengfei.forest.slice.impl;

import java.util.ArrayList;
import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.plotter.HashPlotter;

public class ResourceTribe {

	protected List<SliceResource> availableResources = new ArrayList<>();
	protected List<SliceResource> failResources = new ArrayList<>();
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

	public SliceResource next(long seed) {

		return plotter.to(seed, availableResources, failResources);
	}

	@Override
	public String toString() {
		return "ResourceTribe [availableResources=" + availableResources
				+ ", failResources=" + failResources + ", plotter=" + plotter
				+ "]";
	}
}
