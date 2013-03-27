package fengfei.forest.slice;

import java.util.List;

public interface Plotter {

	int to(long seed, List<SliceResource> availableResources, List<SliceResource> failResources);
}
