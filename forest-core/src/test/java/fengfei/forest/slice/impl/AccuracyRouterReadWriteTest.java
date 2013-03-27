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

public class AccuracyRouterReadWriteTest {

	AccuracyRouter<Long> router = new AccuracyRouter<>();
	int size = 60;

	@Before
	public void setUp() throws Exception {
		router.setOverflowType(OverflowType.Exception);
		int ip = 2;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);
				resource.addExtraInfo(extraInfo());
				//
				Long sliceId = Long.valueOf(i);
				SliceResource sliceResource = new SliceResource(sliceId, resource);
				sliceResource.addParams(extraInfo());
				if (j == 0) {
					sliceResource.setFunction(Function.Write);
				} else {
					sliceResource.setFunction(Function.Read);
				}
				resource.addExtraInfo(extraInfo());
				router.register(sliceId, String.valueOf(i), sliceResource);
			}
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
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size);
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

	@Test
	public void testLocateKeyFunctionForException() {
		String msg = " test locate for exception";
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size) + size;
			try {
				SliceResource resource = router.locate(Long.valueOf(id), Function.Read);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				SliceResource resource = router.locate(Long.valueOf(id), Function.Write);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				SliceResource resource = router.locate(Long.valueOf(id), Function.ReadWrite);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				SliceResource resource = router.locate(Long.valueOf(id));
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testLocateKey() {
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size);
			SliceResource resource = router.locate(Long.valueOf(id));
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
		}
	}
}
