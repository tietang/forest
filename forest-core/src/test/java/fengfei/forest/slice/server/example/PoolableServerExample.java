package fengfei.forest.slice.server.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.impl.HashEqualizer;
import fengfei.forest.slice.server.pool.CommonsPoolableObjectFactory;
import fengfei.forest.slice.server.pool.CommonsPoolableSourceFactory;
import fengfei.forest.slice.server.pool.PoolableException;
import fengfei.forest.slice.server.pool.PoolableServerResource;
import fengfei.forest.slice.server.pool.PoolableServerRouter;
import fengfei.forest.slice.server.pool.PooledSource;
import fengfei.forest.slice.server.pool.ServerHelper;
import fengfei.forest.slice.server.pool.ServerHelper.Clientx;
import fengfei.forest.slice.server.pool.ServerHelper.Serverx;

public class PoolableServerExample {

	static CommonsPoolableSourceFactory<Clientx> commonsPoolableSourceFactory = new CommonsPoolableSourceFactory<>(
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
	static PoolableServerRouter<Long, ServerHelper.Clientx> router;
	static Serverx serverx = new Serverx();
	static int size = 60;

	public static void main(String[] args) throws Exception {
		startServer();
		setupRouter();
		//
		router.setEqualizer(new HashEqualizer<Long>());
		
		for (int i = 0; i < 10; i++) {
			PoolableServerResource<Clientx> resource = router.locate(
					Long.valueOf(i),
					i % 3 == 0 ? Function.Read : Function.Write);
			invoke(resource);
		}
		unsetup();
	}

	private static void invoke(PoolableServerResource<Clientx> resource)
			throws PoolableException {
		System.out.print(String.format(
				"sliceId=%d	alias=%s 	host=%s	rw=%s	",
				resource.getSliceId(),
				resource.getAlias(),
				resource.getHost(),
				resource.getFunction().name()));
		PooledSource<Clientx> source = null;
		Clientx clientx = null;
		try {
			source = resource.getSource();
			clientx = source.getDource();
			String pong = clientx.ping();
			System.out.print("ping:" + pong + "	");
			System.out.println(clientx.getIface().hello("tietang"));
		} catch (Exception e) {
		} finally {
			source.close(clientx);
		}
	}

	public static void unsetup() throws PoolableException {
		serverx.close();
		Map<String, PooledSource<Clientx>> pooledDataSources = router.getPooledDataSources();
		Set<Entry<String, PooledSource<Clientx>>> entries = pooledDataSources.entrySet();
		for (Entry<String, PooledSource<Clientx>> entry : entries) {
			commonsPoolableSourceFactory.destory(entry.getValue());
		}
	}

	private static void setupRouter() {
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
	}

	private static Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("host", "127.0.0.1");
		extraInfo.put("port", "1980");
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		return extraInfo;
	}

	private static void startServer() {
		Thread t = new Thread() {

			public void run() {
				serverx = new Serverx();
				try {
					serverx.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		t.start();
	}
}
