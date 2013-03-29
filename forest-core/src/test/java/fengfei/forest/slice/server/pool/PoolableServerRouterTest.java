package fengfei.forest.slice.server.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.server.pool.ServerHelper.Clientx;

public class PoolableServerRouterTest extends AbstractServerRouterTest {

	Random random = new Random();

	

	@Test
	public void testLocateKeyFunction() throws PoolableException {
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size);
			PoolableServerResource<Clientx> resource = router.locate(
					Long.valueOf(id),
					Function.Read);
			assertNotNull(resource);
			assertEquals(Function.ReadWrite, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			test(resource);
			System.out.println(resource);
			//
			resource = router.locate(Long.valueOf(id), Function.Write);
			assertNotNull(resource);
			assertEquals(Function.ReadWrite, resource.getFunction());
			assertEquals(4, resource.getExtraInfo().size());
			test(resource);
			System.out.println(resource);
			//
			resource = router.locate(Long.valueOf(id), Function.ReadWrite);
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.ReadWrite);
			assertEquals(4, resource.getExtraInfo().size());
			test(resource);
			System.out.println(resource);
		}
	}

	@Test
	public void testLocateKeyFunctionForException() {
		String msg = " test locate for exception";
		router.setOverflowType(OverflowType.Exception);
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size) + size;
			try {
				PoolableServerResource<Clientx> resource = router.locate(
						Long.valueOf(id),
						Function.Read);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				PoolableServerResource<Clientx> resource = router.locate(
						Long.valueOf(id),
						Function.Write);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				PoolableServerResource<Clientx> resource = router.locate(
						Long.valueOf(id),
						Function.ReadWrite);
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
			try {
				PoolableServerResource<Clientx> resource = router.locate(Long.valueOf(id));
				assertTrue(msg, false);
			} catch (Exception e) {
				assertTrue(msg + ":" + e.getMessage(), true);
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testLocateKey() throws PoolableException {
		for (int i = 0; i < size; i++) {
			int id = Math.abs(random.nextInt() % size);
			PoolableServerResource<Clientx> resource = router.locate(Long.valueOf(id));
			assertNotNull(resource);
			assertTrue(resource.getFunction() == Function.ReadWrite);
			assertEquals(4, resource.getExtraInfo().size());
			test(resource);
			System.out.println(resource);
		}
	}
}
