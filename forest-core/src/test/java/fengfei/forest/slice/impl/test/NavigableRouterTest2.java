package fengfei.forest.slice.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.NavigableRouter;

public class NavigableRouterTest2 {
	NavigableRouter<Long> router = new NavigableRouter<>();
	int size = 60;
	int max = 0;

	@Before
	public void setUp() throws Exception {
		int resSize = 10;
		for (long i = 0; i < resSize; i++) {
			String name = "192.168.1." + (i + 2) + ":8002";
			// //System.out.println(name);
			Resource resource = new Resource(name);
			resource.addExtraInfo(extraInfo(i));
			router.register(resource);
		}

		max = size * 1980;
		int ip = 0;
		for (int i = 0; i < size; i++) {
			Long sliceId = Long.valueOf((i + 1) * 1980);

			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + getIp(ip++, resSize) + ":8002";
				if (j == 0) {
					router.map(sliceId, name, Function.Write);
				} else {
					router.map(sliceId, name, Function.Read);
				}
			}
		}
		//System.out.println(router);

	}

	private int getIp(int index, int size) {

		int s = Math.abs(index % size) + 2;
		return s == 0 ? size : s;
	}

	private Map<String, String> extraInfo(long i) {
		String host = "192.168.1." + (i + 2) + ":8002";
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("host", host);
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		extraInfo.put("info1", "v1");
		return extraInfo;
	}

	Random random = new Random();

	@Test
	public void testLocateKeyFunction() {
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % max);
			SliceResource resource = router.locate(Long.valueOf(id),
					Function.Read);
			assertNotNull(resource);
			assertEquals(Function.Read, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);
			SliceResource read = resource;
			//
			resource = router.locate(Long.valueOf(id), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.Write, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			assertNotSame(resource, read);
			//System.out.println(resource);
			// /
			resource = router.locate(Long.valueOf(id), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);

		}

	}

	private void testFirst(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource first = function == null ? router.first() : router
				.first(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, first);
		assertEquals(msg, resource.getSliceId(), first.getSliceId());
	}

	private void testLast(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? router.last() : router
				.last(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, last);
		assertEquals(msg, resource.getSliceId(), last.getSliceId());
	}

	private void testNew(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? router.last() : router
				.last(function);
		SliceResource first = function == null ? router.first() : router
				.first(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, last);
		assertNotNull(msg, first);
		assertNotSame(msg, resource, last);
		assertNotSame(msg, resource, first);
		assertNotSame(msg, resource.getSliceId(), last.getSliceId());
		assertNotSame(msg, resource.getSliceId(), first.getSliceId());
	}

	private void testException(String msg, Long id, Function function) {
		try {
			SliceResource resource = router.locate(Long.valueOf(id), function);
			assertTrue(msg, false);
		} catch (Exception e) {
			assertTrue(msg + ":" + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void testLocateKeyFunctionForOverflow(String msg, Long id,
			Function function) {
		OverflowType overflowType = router.getOverflowType();
		switch (overflowType) {
		case First:
			testFirst(msg, id, function);
			break;
		case Last:
			testLast(msg, id, function);
			break;
		case New:
			testNew(msg, id, function);
			break;
		case Exception:

			testException(msg, id, function);
			break;
		default:
			testException(msg, id, function);
			break;
		}
	}

	@Test
	public void testLocateKeyFunctionForException() {
		String msg = " test locate for exception";
		for (int i = 0; i < size; i++) {
			Long id = Long.valueOf(Math.abs(random.nextInt() % max) + max + 1);

			testLocateKeyFunctionForOverflow(msg, id, Function.Read);
			testLocateKeyFunctionForOverflow(msg, id, Function.Write);
			testLocateKeyFunctionForOverflow(msg, id, Function.ReadWrite);

		}

	}

	@Test
	public void testLocateKey() {
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % max);
			SliceResource resource = router.locate(Long.valueOf(id));
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);

		}
	}

}
