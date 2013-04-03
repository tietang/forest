package fengfei.forest.slice.plotter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;

public class LoopPlotter implements Plotter {

	final AtomicInteger count = new AtomicInteger();
	private int currentIndex;

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources,
			List<SliceResource> failResources) {

		int length = availableResources.size(); // 总个数
		int totalWeight = 0;
		int maxWeight = 0; // 最大权重
		int minWeight = Integer.MAX_VALUE; // 最小权重
		for (int i = 0; i < length; i++) {
			SliceResource res = availableResources.get(i);
			int weight = res.getWeight();
			totalWeight += weight;
			maxWeight = Math.max(maxWeight, weight); // 累计最大权重
			minWeight = Math.min(minWeight, weight); // 累计最小权重
		}

		currentIndex = count.getAndIncrement();
		if (maxWeight > 0 && minWeight < maxWeight) { // 权重不一样
			int currentWeight = currentIndex % totalWeight;
			List<SliceResource> weightResources = new ArrayList<>();
			for (SliceResource res : weightResources) { // 筛选权重大于当前权重基数的
														// Resource
				if (res.getWeight() > currentWeight) {
					weightResources.add(res);
				}
			}
			int weightLength = weightResources.size();
			if (weightLength == 1) {
				return weightResources.get(0);
			} else if (weightLength > 1) {
				length = weightLength;
			}
		}
		int index = Math.abs(currentIndex % length);
		return availableResources.get(index);

	}

	public int getCurrentIndex() {
		return currentIndex;
	}
}
