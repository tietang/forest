package fengfei.forest.slice.plotter;

import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;

public class RemainderPlotter implements Plotter {

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources, List<SliceResource> failResources) {
		int index = Math.abs((int) (seed % availableResources.size()));
		return availableResources.get(index);
	}
}
