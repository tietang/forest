package fengfei.forest.slice.database;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;

public class DatabaseRouterFactoryTest {
	static DatabaseRouterFactory factory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		XmlSliceConfigReader reader = new XmlSliceConfigReader("cp:config.xml");

		Config config = reader.read("/root/main");
//		System.out.println(config);
		factory = new DatabaseRouterFactory(config);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void test() {
		PoolableDatabaseRouter<Long> router = factory.getPoolableRouter("r01");
		Assert.assertNotNull(router);
		router = factory.getPoolableRouter("r02");
		Assert.assertNotNull(router);
		PoolableDatabaseResource resource=router.locate(1l);
	}

}
