package fengfei.forest.slice.fail;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import fengfei.forest.slice.Detector;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;

public class AutoFailoverThread<Key> extends Thread {
	Router<Key> router;

	@Override
	public void run() {
		Detector detector = router.getDetector();
		Map<Long, Slice<Key>> slices = router.getSlices();
		Set<Entry<Long, Slice<Key>>> entries = slices.entrySet();

	}
}
