package fengfei.forest.slice.server.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.server.pool.ServerHelper.Clientx;
import fengfei.forest.slice.server.pool.ServerHelper.Serverx;

public class AbstractServerRouterTest {

	static CommonsPoolableSourceFactory<Clientx> commonsPoolableSourceFactory = null;
	static PoolableServerRouter<Long, ServerHelper.Clientx> router;
	static Serverx serverx = new Serverx();
	static int size = 60;

	@BeforeClass
	public static void setup() {
		Thread t = new Thread() {

			public void run() {
				try {
					serverx.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		t.start();
		commonsPoolableSourceFactory = new CommonsPoolableSourceFactory<>(
				new CommonsPoolableObjectFactory<Clientx>() {

					@Override
					public Clientx makeObject() throws Exception {
						return new Clientx(host, port);
					}

					@Override
					public void destroyObject(Clientx obj) throws Exception {
						obj.close();
					}

					@Override
					public boolean validateObject(Clientx obj) {
						try {
							return "pong".equals(obj.ping());
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}

					@Override
					public void activateObject(Clientx obj) throws Exception {
					}

					@Override
					public void passivateObject(Clientx obj) throws Exception {
					}
				});
		
		AccuracyRouter<Long> facade = new AccuracyRouter<>();
		router = new PoolableServerRouter<Long, ServerHelper.Clientx>(
				facade,
				commonsPoolableSourceFactory);
		
		int resSize = 10;
		for (long i = 0; i < resSize; i++) {
			String name = "127.0.0.1:1980";
			// System.out.println(name);
			Resource resource = new Resource(name);
			resource.addExtraInfo(extraInfo());
			router.register(resource);
		}
		int ip = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "127.0.0.1:1980";
				if (j == 0) {
					router.map(Long.valueOf(i), name, Function.ReadWrite);
				} else {
					router.map(Long.valueOf(i), name, Function.ReadWrite);
				}
			}
			// System.out.println(name);
		}
		System.out.println(router);
	}

	@AfterClass
	public static void unsetup() throws PoolableException {
		serverx.close();
		Map<String, PooledSource<Clientx>> pooledDataSources = router.getPooledDataSources();
		Set<Entry<String, PooledSource<Clientx>>> entries = pooledDataSources.entrySet();
		for (Entry<String, PooledSource<Clientx>> entry : entries) {
			commonsPoolableSourceFactory.destory(entry.getValue());
		}
	}

	private int getIp(int index, int size) {
		int s = Math.abs(index % size) + 2;
		return s == 0 ? size : s;
	}

	private static Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("host", "127.0.0.1");
		extraInfo.put("port", "1980");
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		return extraInfo;
	}

	protected void test(PoolableServerResource<Clientx> resource) throws PoolableException {
		PooledSource<Clientx> source = null;
		Clientx clientx = null;
		try {
			source = resource.getSource();
			clientx = source.getDource();
			String pong = clientx.ping();
			assertEquals("pong", pong);
		} catch (Exception e) {
			assertTrue(false);
		} finally {
			source.close(clientx);
		}
	}
}
