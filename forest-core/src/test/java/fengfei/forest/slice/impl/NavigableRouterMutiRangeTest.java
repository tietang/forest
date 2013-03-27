package fengfei.forest.slice.impl;

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
import fengfei.forest.slice.Range;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class NavigableRouterMutiRangeTest {

	NavigableRouter<Long> router = new NavigableRouter<>();
	int x = 2;
	int[] includes = { 1, 3, 1000, 2001, 2303, 3000, 4001, 4309, 5000, 1000001, 1001230, 1002000, 1004001, 1005006, 1006000, 1008001, 1008234, 100900 };
	int[] excludes = { 0, 1001, 1202, 2000, 3001, 3210, 4000, 5001, 5129, 1000000, 1002001, 1003021, 1004000, 1006001, 1006790, 1008000, 1009001, 2000000, 123456790 };

	@Before
	public void setUp() throws Exception {
		// [0, 1000, 2000, 3000, 4000, 5000, 1000000, 1002000, 1004000, 1006000,
		// 1008000, 1009000]
		register(1,new Range(1, 1000), new Range(1000001, 1002000));
		register(2,new Range(2001, 3000), new Range(1004001, 1006000));
		register(3,new Range(4001, 5000), new Range(1008001, 1009000));
		router.setOverflowType(OverflowType.First);
		System.out.println(router);
	}

	private void register(int i,Range... ranges) {
		for (int j = 0; j < 3; j++) {
			String name = "192.168.1." + (x++) + ":8002";
			Resource resource = new Resource(name);
			resource.addExtraInfo(extraInfo());
			//
			SliceResource sliceResource = new SliceResource(resource);
			sliceResource.addParams(extraInfo());
			if (j == 0) {
				sliceResource.setFunction(Function.Write);
			} else {
				sliceResource.setFunction(Function.Read);
			}
			resource.addExtraInfo(extraInfo());
			router.register(sliceResource, String.valueOf(i), ranges);
		}
	}

	private Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("info1", "info1 value");
		extraInfo.put("info2", "info2 value");
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		return extraInfo;
	}

	Random random = new Random();

	@Test
	public void testLocateKeyFunction() {
		for (int id : includes) {
			SliceResource resource = router.locate(Long.valueOf(id), Function.Read);
			assertNotNull(resource);
			assertEquals(Function.Read, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
			SliceResource read = resource;
			//
			resource = router.locate(Long.valueOf(id), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.Write, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			assertNotSame(resource, read);
			System.out.println(resource);
			// /
			resource = router.locate(Long.valueOf(id), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
		}
	}

	private void testFirst(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource first = function == null ? router.first() : router.first(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, first);
		assertEquals(msg, resource.getSliceId(), first.getSliceId());
	}

	private void testLast(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? router.last() : router.last(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, last);
		System.out.println(id + " res: " + resource);
		System.out.println(id + " last: " + last);
		assertEquals(msg, resource.getSliceId(), last.getSliceId());
	}

	private void testNew(String msg, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? router.last() : router.last(function);
		SliceResource first = function == null ? router.first() : router.first(function);
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

	private void testLocateKeyFunctionForOverflow(String msg, Long id, Function function) {
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
		for (int id : excludes) {
			Long id1 = Long.valueOf(id);
			testLocateKeyFunctionForOverflow(msg, id1, Function.Read);
			testLocateKeyFunctionForOverflow(msg, id1, Function.Write);
			testLocateKeyFunctionForOverflow(msg, id1, Function.ReadWrite);
		}
	}

	@Test
	public void testLocateKey() {
		for (int id : excludes) {
			SliceResource resource = router.locate(Long.valueOf(id));
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
		}
	}
}
