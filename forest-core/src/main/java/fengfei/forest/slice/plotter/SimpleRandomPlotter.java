package fengfei.forest.slice.plotter;

import java.util.List;
import java.util.Random;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;

public class SimpleRandomPlotter implements Plotter {

	Random random = new Random(19800202);

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources,
			List<SliceResource> failResources) {
		int length = availableResources.size();
		int index = random.nextInt(length - 1);
		return availableResources.get(index);
	}

}
