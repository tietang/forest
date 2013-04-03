package fengfei.forest.slice;

import java.util.List;
/**
 * LoadBalance
 * @author tietang
 *
 */
public interface Plotter {

	SliceResource to(long seed, List<SliceResource> availableResources, List<SliceResource> failResources);
}
