package fengfei.exmaple;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.impl.DefaultForestGrower;
import fengfei.forest.database.pool.TomcatPoolableDataSourceFactory;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.database.PoolableDatabaseResource;
import fengfei.forest.slice.database.PoolableDatabaseRouter;
import fengfei.forest.slice.database.UrlMaker;
import fengfei.forest.slice.database.url.MysqlUrlMaker;
import fengfei.forest.slice.impl.NavigableRouter;
import fengfei.forest.slice.plotter.HashPlotter;

public class RangedExample {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PoolableDatabaseRouter<Long> router = new PoolableDatabaseRouter<>(
				new NavigableRouter<Long>(), new MysqlUrlMaker(),
				new TomcatPoolableDataSourceFactory());

		router.setPlotter(new HashPlotter());
		router.setOverflowType(OverflowType.Last);
		router.setEqualizer(new Equalizer<Long>() {
			@Override
			public long get(Long key, int sliceSize) {
				return key;
			}
		});

		setupGroup(router);
		setup(router);
		testWrite(router);
		testRead(router);
		testRead2(router);
		test(router);

	}

	private static void testWrite(PoolableDatabaseRouter<Long> router)
			throws SQLException {
		PoolableDatabaseResource resource = router.locate(101l, Function.Write);
		String suffix = resource.getAlias();
		DataSource ds = resource.getDataSource();
		try (Connection conn = ds.getConnection();) {
			ForestGrower grower = new DefaultForestGrower(conn);

			int updated = UserDao.save(grower, suffix, new User("test@163.com",
					"testname", "123456"));
			System.out.println("updated: " + updated);
		}
	}

	private static void testRead(PoolableDatabaseRouter<Long> router)
			throws SQLException {
		PoolableDatabaseResource resource = router.locate(101l, Function.Read);
		String suffix = resource.getAlias();
		DataSource ds = resource.getDataSource();
		try (Connection conn = ds.getConnection();) {
			ForestGrower grower = new DefaultForestGrower(conn);
			User user = UserDao.get(grower, suffix, 1);
			System.out.println("get user:" + user);
		}
	}

	private static void testRead2(PoolableDatabaseRouter<Long> router)
			throws SQLException {
		PoolableDatabaseResource resource = router.locate(101l, Function.Read);
		String suffix = resource.getAlias();
		DataSource ds = resource.getDataSource();
		try (Connection conn = ds.getConnection();) {
			ForestGrower grower = new DefaultForestGrower(conn);
			User user = UserDao.get(grower, suffix, "test@163.com",
					"123456");
			System.out.println("get user:" + user);
		}
	}

	private static void test(PoolableDatabaseRouter<Long> router)
			throws SQLException {

		PoolableDatabaseResource resource = router.locate(101l, Function.Read);
		String suffix = resource.getAlias();
		DataSource ds = resource.getDataSource();
		try (Connection conn = ds.getConnection();) {
			ForestGrower grower = new DefaultForestGrower(conn);
			boolean isExist = UserDao.isExists(grower, suffix, "testname");
			System.out.println("isExist:" + isExist);
		}
	}

	private static void setupGroup(Router<Long> router) {
		int ip = 2;
		int maxMod = 1024;
		int sliceSize = 3;
		for (int i = 0; i < sliceSize; i++) {
			Long sliceId = Long.valueOf((i + 1) * 1980);
			for (int j = 0; j < 6; j++) {
				// String name = "192.168.1." + (ip++) + ":3306";
				String name = "s2.to:3306";
				Resource resource = new Resource(name);
				SliceResource sliceResource = new SliceResource(resource);
				sliceResource.setFunction(j < 2 ? Function.Write
						: Function.Read);
				sliceResource.addParams(extraInfo());
				router.register(sliceId, String.valueOf(i), sliceResource);
			}
		}

	}

	private static void setup(PoolableDatabaseRouter<Long> router) {
		router.followSetup();
		Map<Long, Slice<Long>> slices = router.getSlices();
		Set<Entry<Long, Slice<Long>>> entries = slices.entrySet();
		for (Entry<Long, Slice<Long>> entry : entries) {
			Slice<Long> slice = entry.getValue();
			SliceResource facade = slice.get(10L, Function.Write);
			PoolableDatabaseResource resource = new PoolableDatabaseResource(
					facade);
			UrlMaker urlMaker = router.getUrlMaker();
			String url = resource.getURL();
			url = url == null ? urlMaker.makeUrl(resource) : url;
			DataSource ds = router.allPooledDataSources().get(url);
			try {
				String suffix = resource.getAlias();
				execute(String.format(DropTable, suffix), ds);
				execute(String.format(CreateTable, suffix), ds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void execute(String sql, DataSource ds) throws SQLException {
		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement();) {
			boolean executed = stmt.execute(sql);
		}

	}

	private static Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("driverClass", "com.mysql.jdbc.Driver");
		extraInfo.put("username", "test");
		extraInfo.put("password", "test");
		extraInfo.put("host", "s2.to");
		extraInfo.put("port", "3306");
		extraInfo.put("schema", "test");
		// pool config
		extraInfo.put("maxActive", "10");
		extraInfo.put("maxIdle", "10");
		extraInfo.put("minIdle", "1");
		extraInfo.put("initialSize", "2");
		extraInfo.put("maxWait", "30000");
		extraInfo.put("testOnBorrow", "true");
		return extraInfo;
	}

	static final String DropTable = "DROP TABLE IF EXISTS `user%s` ;";
	static final String CreateTable = "CREATE TABLE `user%s` ("
			+ " id_user int(11) NOT NULL AUTO_INCREMENT,"
			+ "  email varchar(32) NOT NULL,"
			+ "  username varchar(32) NOT NULL,"
			+ "  password varchar(32) DEFAULT NULL,"
			+ "  PRIMARY KEY (`id_user`),"
			+ "  UNIQUE KEY `email_UNIQUE` (`email`),"
			+ "  UNIQUE KEY `username_UNIQUE` (`username`)"
			+ " ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
}
