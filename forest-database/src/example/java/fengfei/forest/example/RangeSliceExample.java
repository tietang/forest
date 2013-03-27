package fengfei.forest.example;

import java.util.HashMap;
import java.util.Map;

import fengfei.forest.database.pool.TomcatPoolableDataSourceFactory;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.database.MysqlConnectonUrlMaker;
import fengfei.forest.slice.database.PoolableDatabaseRouter;
import fengfei.forest.slice.impl.NavigableRouter;
import fengfei.forest.slice.utils.HashAlgorithms;

public class RangeSliceExample {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		NavigableRouter<Long> faced = new NavigableRouter<>();
		faced.setSelectType(SelectType.Hash);
		faced.setOverflowType(OverflowType.Last);
		faced.setEqualizer(new Equalizer<Long>() {

			int max = 1024;

			@Override
			public long get(Long key, int sliceSize) {
				String data = key.toString();
				return Math.abs(HashAlgorithms.FNVHash1(data) % sliceSize) + 1;
			}
		});
		PoolableDatabaseRouter<Long> router = new PoolableDatabaseRouter<>(
				faced,
				new MysqlConnectonUrlMaker(),
				new TomcatPoolableDataSourceFactory());
		setupGroup(router);
		System.out.println(router);
		System.out.println(router.locate(101l).getDataSource().toString());
		System.out.println(router.locate(2l));
		System.out.println(router.locate(2635l));
		System.out.println(router.locate(20000l));
		System.out.println(router.locate(12l, Function.Read));
		System.out.println(router.locate(2896l, Function.Read));
		System.out.println(router.locate(19l, Function.Read));
		System.out.println(router.locate(24642l, Function.Read));
		System.out.println(router.locate(9912l, Function.Write));
		System.out.println(router.locate(9720l, Function.Write));
		System.out.println(router.locate(9701l, Function.Write));
		System.out.println(router.locate(11000l, Function.Write));
	}

	private static void setupGroup(Router<Long> router) {
		int ip = 2;
		int maxMod = 1024;
		int sliceSize = 20;
		for (int i = 0; i < sliceSize; i++) {
			Long sliceId = Long.valueOf((i + 1) * 1980);
			for (int j = 0; j < 6; j++) {
				// String name = "192.168.1." + (ip++) + ":3306";
				String name = "127.0.0.1:3306";
				Resource resource = new Resource(name);
				resource.addExtraInfo(extraInfo());
				SliceResource sliceResource = new SliceResource(resource);
				sliceResource.setFunction(j < 2 ? Function.Write : Function.Read);
				sliceResource.addParams(extraInfo());
				router.register(sliceId, String.valueOf(i), sliceResource);
				System.out.println(sliceResource);
			}
		}
	}

	private static Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("driverClass", "com.mysql.jdbc.Driver");
		extraInfo.put("user", "root");
		extraInfo.put("password", "");
		// pool config
		extraInfo.put("maxActive", "10");
		extraInfo.put("maxIdle", "10");
		extraInfo.put("minIdle", "1");
		extraInfo.put("initialSize", "2");
		extraInfo.put("maxWait", "30000");
		extraInfo.put("testOnBorrow", "true");
		return extraInfo;
	}
}
