package fengfei.forest.slice.plotter;

import java.util.List;
import java.util.Random;

import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.SliceResource;

public class RandomPlotter implements Plotter {

	Random random = new Random(19800202);

	@Override
	public SliceResource to(long seed, List<SliceResource> availableResources,
			List<SliceResource> failResources) {
		int length = availableResources.size();
		int totalWeight = 0;
		boolean sameWeight = true;
		int lastWeight = 0;
		for (int i = 0; i < length; i++) {
			SliceResource res = availableResources.get(i);
			int weight = res.getWeight();
			totalWeight += weight;
			lastWeight = weight;
			if (sameWeight && i > 0 && weight != lastWeight) {
				sameWeight = false;
			}
		}
		if (totalWeight > 0 && !sameWeight) {
			// 如果权重不相同且权重大于0则按总权重数随机
			int offset = random.nextInt(totalWeight);
			// 并确定随机值落在哪个片断上
			for (int i = 0; i < length; i++) {
				SliceResource res = availableResources.get(i);
				offset -= res.getWeight();
				if (offset < 0) {
					return availableResources.get(i);
				}
			}
		}
		// 如果权重相同或权重为0则均等随机
		int index= random.nextInt(length);
		return availableResources.get(index);

	}

}
