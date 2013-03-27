package fengfei.forest.slice.impl;

import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Resource;

public class RemainderPlotter implements Plotter {

	@Override
	public int to(long seed, List<Resource> availableResources, List<Resource> failResources) {
		int index = Math.abs((int) (seed % availableResources.size()));
		return index;
	}
}
