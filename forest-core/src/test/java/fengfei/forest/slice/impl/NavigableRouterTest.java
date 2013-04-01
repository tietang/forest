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
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class NavigableRouterTest {

	NavigableRouter<Long> navigator = new NavigableRouter<>();
	int size = 60;
	int max = 0;

	@Before
	public void setUp() throws Exception {
		int x = 2;
		max = size * 1980;
		for (int i = 0; i < size; i++) {
			Long sliceId = Long.valueOf((i + 1) * 1980);
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (x++) + ":8002";
				Resource resource = new Resource(name);
				resource.addExtraInfo(extraInfo());
				SliceResource sliceResource = new SliceResource(resource);
				if (j == 0) {
					sliceResource.setFunction(Function.Write);
				} else {
					sliceResource.setFunction(Function.Read);
				}
				sliceResource.addParams(extraInfo());
				navigator.register(sliceId, String.valueOf(i), sliceResource);
			}
		}
		//System.out.println(navigator);
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
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % max);
			SliceResource resource = navigator.locate(Long.valueOf(id), Function.Read);
			assertNotNull(resource);
			assertEquals(Function.Read, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);
			SliceResource read = resource;
			//
			resource = navigator.locate(Long.valueOf(id), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.Write, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			assertNotSame(resource, read);
			//System.out.println(resource);
			// /
			resource = navigator.locate(Long.valueOf(id), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);
		}
	}

	private void testFirst(String msg, Long id, Function function) {
		SliceResource resource = navigator.locate(Long.valueOf(id), function);
		SliceResource first = function == null ? navigator.first() : navigator
				.first(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, first);
		assertEquals(msg, resource.getSliceId(), first.getSliceId());
	}

	private void testLast(String msg, Long id, Function function) {
		SliceResource resource = navigator.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? navigator.last() : navigator.last(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, last);
		assertEquals(msg, resource.getSliceId(), last.getSliceId());
	}

	private void testNew(String msg, Long id, Function function) {
		SliceResource resource = navigator.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? navigator.last() : navigator.last(function);
		SliceResource first = function == null ? navigator.first() : navigator
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
			SliceResource resource = navigator.locate(Long.valueOf(id), function);
			assertTrue(msg, false);
		} catch (Exception e) {
			assertTrue(msg + ":" + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void testLocateKeyFunctionForOverflow(String msg, Long id, Function function) {
		OverflowType overflowType = navigator.getOverflowType();
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
			SliceResource resource = navigator.locate(Long.valueOf(id));
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			//System.out.println(resource);
		}
	}
}
