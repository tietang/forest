package fengfei.forest.slice.impl;

import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.utils.HashAlgorithms;

public class HashPlotter implements Plotter {

	@Override
	public int to(long seed, List<Resource> availableResources, List<Resource> failResources) {
		int index = HashAlgorithms.FNVHash1(String.valueOf(seed)) % availableResources.size();
		return index;
	}
}
