package fengfei.forest.slice.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class AbstractRouterTest {

	protected boolean isReadWrite = false;

	protected Map<String, String> extraInfo(String host, int port) {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("host", host);
		extraInfo.put("port", String.valueOf(port));
		extraInfo.put("user", "root");
		extraInfo.put("password", "");
		return extraInfo;
	}

	Random random = new Random();

	protected void testLocateKeyFunction(Router<Long> router, int id) {
		//
		SliceResource resource = router.locate(Long.valueOf(id), Function.Read);
		assertNotNull(resource);
		assertEquals(isReadWrite ? Function.ReadWrite : Function.Read, resource.getFunction());
		assertEquals(4, resource.getExtraInfo().size());
		//System.out.println(resource);
		SliceResource read = resource;
		//
		resource = router.locate(Long.valueOf(id), Function.Write);
		assertNotNull(resource);
		assertEquals(
				isReadWrite ? Function.ReadWrite : Function.Write,
				resource.getFunction());
		assertEquals(4, resource.getExtraInfo().size());
		if (!isReadWrite) {
			assertNotSame(resource, read);
		}
		//System.out.println(resource);
		// /
		resource = router.locate(Long.valueOf(id), Function.ReadWrite);
		assertNotNull(resource);
		if (isReadWrite) {
			assertTrue(resource.getFunction() == Function.ReadWrite);
		} else {
			assertTrue(resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
		}
		assertEquals(4, resource.getExtraInfo().size());
		//System.out.println(resource);
	}

	protected void testFirst(String msg, Router<Long> router, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource first = function == null ? router.first() : router.first(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, first);
		assertEquals(msg, resource.getSliceId(), first.getSliceId());
	}

	protected void testLast(String msg, Router<Long> router, Long id, Function function) {
		SliceResource resource = router.locate(Long.valueOf(id), function);
		SliceResource last = function == null ? router.last() : router.last(function);
		assertNotNull(msg, resource);
		assertNotNull(msg, last);
		assertEquals(msg, resource.getSliceId(), last.getSliceId());
	}

	protected void testNew(String msg, Router<Long> router, Long id, Function function) {
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

	protected void testException(String msg, Router<Long> router, Long id, Function function) {
		try {
			SliceResource resource = router.locate(Long.valueOf(id), function);
			assertTrue(msg, false);
		} catch (Exception e) {
			assertTrue(msg + ":" + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	protected void testLocateKeyFunctionForOverflow(
			String msg,
			Router<Long> router,
			Long id,
			Function function) {
		OverflowType overflowType = router.getOverflowType();
		switch (overflowType) {
		case First:
			testFirst(msg, router, id, function);
			break;
		case Last:
			testLast(msg, router, id, function);
			break;
		case New:
			testNew(msg, router, id, function);
			break;
		case Exception:
			testException(msg, router, id, function);
			break;
		default:
			testException(msg, router, id, function);
			break;
		}
	}

	protected void testLocateKeyFunctionForException(Router<Long> router, int id) {
		String msg = " test locate for exception";
		Long id1 = Long.valueOf(id);
		testLocateKeyFunctionForOverflow(msg, router, id1, Function.Read);
		testLocateKeyFunctionForOverflow(msg, router, id1, Function.Write);
		testLocateKeyFunctionForOverflow(msg, router, id1, Function.ReadWrite);
	}

	protected void testLocateKey(Router<Long> router, int id) {
		String msg = " test locate for exception for id=" + id;
		SliceResource resource = router.locate(Long.valueOf(id));
		assertNotNull(msg, resource);
		if (isReadWrite) {
			assertTrue(msg, resource.getFunction() == Function.ReadWrite);
		} else {
			assertTrue(
					msg,
					resource.getFunction() == Function.Read || resource.getFunction() == Function.Write);
		}
		assertEquals(msg, 4, resource.getExtraInfo().size());
		//System.out.println(resource);
	}
}
