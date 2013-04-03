package fengfei.forest.slice.plotter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;

public class SimpleLoopPlotter implements Plotter {

	final AtomicInteger count = new AtomicInteger();
	private int currentIndex;

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources, List<SliceResource> failResources) {
		currentIndex = count.getAndIncrement();
		int index = Math.abs(currentIndex % availableResources.size());
		return availableResources.get(index);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}
}
