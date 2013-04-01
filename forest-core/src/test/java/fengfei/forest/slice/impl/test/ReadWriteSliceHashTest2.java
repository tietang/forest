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

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.ReadWriteSlice;

public class ReadWriteSliceHashTest2 {

	Slice<Long> slice;

	@Before
	public void setup() {
		Long sliceId = 0l;
		slice = new ReadWriteSlice<>(sliceId);

		Map<String, String> extraInfo = extraInfo();
		for (int i = 0; i < 10; i++) {
			slice.addParams(extraInfo);
			String host = "192.168.1." + (i + 2) + ":8080";

			Resource resource = new Resource(host);
			Function function = null;
			if (i <= 1) {
				function = Function.Write;
			} else {
				function = Function.Read;
			}
			SliceResource sliceResource = new SliceResource(sliceId, function,
					resource);
			slice.add(sliceResource);
		}
		//System.out.println(slice);
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

		Random rd = new Random();
		for (int i = 0; i < 20; i++) {

			SliceResource resource = slice.get(rd.nextInt(), Function.Read);
			assertNotNull(resource);
			assertEquals(Function.Read, resource.getFunction());
			assertEquals(4, resource.getParams().size());
			//System.out.println("read: " + resource);
			SliceResource read = resource;
			//

			resource = slice.get(rd.nextInt(), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.Write, resource.getFunction());
			assertEquals(4, resource.getParams().size());
			//System.out.println("write: " + resource);
			assertNotSame(resource, read);
			//
			resource = slice.get(rd.nextInt(), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getParams().size());
			//System.out.println("ReadWrite(Any):" + resource);
		}
		//System.out.println();
	}

	Random random = new Random();

	@Test
	public void testAny() {
		//System.out.println("test any");

		for (int i = 0; i < 20; i++) {
			SliceResource resource = slice.getAny(random.nextLong());

			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.Read
					|| resource.getFunction() == Function.Write);
			assertEquals(4, resource.getParams().size());
			//System.out.println(resource);

		}
	}
}
