package fengfei.forest.slice.impl;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;

public class AccuracyRouterEqualityTest extends AbstractRouterTest {

	AccuracyRouter<Long> router = new AccuracyRouter<>();
	int size = 60;

	@Before
	public void setUp() throws Exception {
		isReadWrite = true;
		router.setOverflowType(OverflowType.Exception);
		int ip = 2;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);
				Long sliceId = Long.valueOf(i);
				SliceResource sliceResource = new SliceResource(sliceId, resource);
				sliceResource.addParams(extraInfo("192.168.1." + ip, 8002));
				router.register(Long.valueOf(i), String.valueOf(i), sliceResource);
			}
		}
	}

	Random random = new Random();

	@Test
	public void testLocateKeyFunction() {
		for (int i = 0; i < size; i++) {
			testLocateKeyFunction(router, i);
		}
	}

	@Test
	public void testLocateKeyFunctionForException() {
		router.setOverflowType(OverflowType.Exception);
		int id = Math.abs(random.nextInt() % size) + size;
		testLocateKeyFunctionForException(router, id);
		//
		router.setOverflowType(OverflowType.Last);
		id = Math.abs(random.nextInt() % size) + size;
		testLocateKeyFunctionForException(router, id);
		//
		router.setOverflowType(OverflowType.First);
		id = Math.abs(random.nextInt() % size) + size;
		testLocateKeyFunctionForException(router, id);
		//
	}

	@Test
	public void testLocateKey() {
		for (int i = 0; i < size; i++) {
			testLocateKey(router, i);
		}
	}

	public void setRouter(AccuracyRouter<Long> router) {
		this.router = router;
	}
}
