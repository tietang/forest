package fengfei.forest.slice.plotter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;

@Ignore
public class LoopPlotterTest {
	static LoopPlotter plotter = new LoopPlotter();
	static List<SliceResource> weightResources = new ArrayList<>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int ip = 2;
		int size = 10;
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			int weight = Math.abs(random.nextInt(10));
			String name = weight + ":192.168.1." + (ip++) + ":8002";

			Resource resource = new Resource(name);
			resource.setWeight(weight);
			Long sliceId = Long.valueOf(i);
			SliceResource sliceResource = new SliceResource(sliceId, resource);
			weightResources.add(sliceResource);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Map<String, AtomicInteger> map = new HashMap<>();

		for (int i = 0; i < 1000; i++) {
			SliceResource resource = plotter.to(i, weightResources,
					weightResources);
			incr(map, resource);

		}
		Set<Entry<String, AtomicInteger>> entries = map.entrySet();
		for (Entry<String, AtomicInteger> entry : entries) {
			System.out.println(String.format("%s=%d", entry.getKey(), entry
					.getValue().get()));
		}
	}

	private void incr(Map<String, AtomicInteger> map, SliceResource resource) {
		AtomicInteger ai = map.get(resource.getName());
		if (ai == null) {
			ai = new AtomicInteger(0);
		}
		ai.getAndIncrement();

		map.put(resource.getName(), ai);
	}

}
