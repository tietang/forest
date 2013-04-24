package fengfei.forest.slice.config;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;

public class DefaultRouterFactoryTest {

	static DefaultRouterFactory factory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		XmlSliceConfigReader reader = new XmlSliceConfigReader("cp:config.xml");

		Config config = reader.read("/root/main");
		factory = new DefaultRouterFactory(config);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void test() {
		Router<Long> router = factory.getRouter("r01");
		Assert.assertNotNull(router);
	}

}
