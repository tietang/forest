package fengfei.forest.slice.fail;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.slice.Detector;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.impl.ResourceTribe;

public class AutoFailoverThread<Key> extends Thread {
	static Logger log = LoggerFactory.getLogger(AutoFailoverThread.class);
	/**
	 * interval seconds
	 */
	int interval = 3;
	Router<Key> router;

	public AutoFailoverThread(int interval, Router<Key> router) {
		super();
		this.interval = interval;
		this.router = router;
	}

	public AutoFailoverThread(Router<Key> router) {
		super();
		this.router = router;
	}

	@Override
	public void run() {
		while (true) {
			Detector detector = router.getDetector();
			Map<Long, Slice<Key>> slices = router.getSlices();
			Set<Entry<Long, Slice<Key>>> entries = slices.entrySet();
			for (Entry<Long, Slice<Key>> entry : entries) {
				Slice<Key> slice = entry.getValue();
				ResourceTribe tribe = slice.getTribe();
				fail(detector, slice, tribe);
				recover(detector, slice, tribe);
			}
			try {
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				log.error("sleep", e);
			}
		}
	}

	private void fail(Detector detector, Slice<Key> slice, ResourceTribe tribe) {
		List<SliceResource> resources = tribe.getAvailableResources();
		for (SliceResource resource : resources) {
			try {
				if (!detector.detect(resource)) {
					slice.fail(resource);
				}
			} catch (Exception e) {
				log.error("detect resource for fail:" + resource.getName(), e);
			}
		}
	}

	private void recover(Detector detector, Slice<Key> slice,
			ResourceTribe tribe) {
		List<SliceResource> resources = tribe.getFailResources();
		for (SliceResource resource : resources) {
			try {
				if (detector.detect(resource)) {
					slice.recover(resource);
				}
			} catch (Exception e) {
				log.error("detect resource for recover:" + resource.getName(),
						e);
			}
		}
	}
}
