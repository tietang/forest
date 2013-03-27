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

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Resource.Function;
import fengfei.forest.slice.Slice;

public class ReadWriteSliceHashTest {

	Slice<Long> slice;

	@Before
	public void setup() {
		slice = new ReadWriteSlice<>();

		Map<String, String> extraInfo = extraInfo();
		for (int i = 0; i < 10; i++) {
			slice.addExtraInfo(extraInfo);
			String host = "192.168.1." + (i + 2) + ":8080";

			Resource resource = new Resource(host);
			if (i <= 1) {
				resource.setFunction(Function.Write);
			} else {
				resource.setFunction(Function.Read);
			}
			slice.add(resource);
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

	@Test
	public void test() {
		System.out.println("test read");
		Random rd = new Random();
		for (int i = 0; i < 20; i++) {

			Resource resource = slice.get(rd.nextInt(), Function.Read);

			assertNotNull(resource);
			assertEquals(Function.Read, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
			Resource read = resource;
			//
			resource = slice.get(rd.nextInt(), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.Write, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
			assertNotSame(resource, read);
			//
			resource = slice.get(rd.nextInt(), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);
		}
		System.out.println();
	}

	Random random = new Random();

	@Test
	public void testAny() {
		System.out.println("test any");

		for (int i = 0; i < 20; i++) {
			Resource resource = slice.getAny(random.nextLong());
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getExtraInfo().size());
			System.out.println(resource);

		}
	}
}
