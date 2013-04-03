package fengfei.forest.slice.plotter;

import java.util.List;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.utils.HashAlgorithms;

public class HashPlotter implements Plotter {

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources,
			List<SliceResource> failResources) {
		int index = Math.abs(HashAlgorithms.FNVHash1(String.valueOf(seed))
				% availableResources.size());
		return availableResources.get(index);
	}
}
