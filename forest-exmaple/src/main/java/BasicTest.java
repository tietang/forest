import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.SliceConfigReader;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;
import fengfei.forest.slice.config.zk.ZKSliceConfigReader;
import fengfei.forest.slice.database.DatabaseRouterFactory;

public class BasicTest {
	static Logger logger = LoggerFactory.getLogger(BasicTest.class);

	public static void main(String[] args) {
		DatabaseRouterFactory databaseRouterFactory;
		SliceConfigReader configReader;

		String sliceConfig = "file/cp:config.xml";
		if (sliceConfig != null && !"".equals(sliceConfig)) {
			String[] scc = sliceConfig.split("/");
			System.out.println("scc: " + scc.length);
			String file = scc[1];
			if (scc[0].equals("file")) {
				configReader = new XmlSliceConfigReader(file);
			} else {
				configReader = new ZKSliceConfigReader(file);
			}
			try {

				logger.info("reading xml config..." + file);
				Config config = configReader.read("/root");
				logger.info("pasering config....");
				System.out.println(config);
				databaseRouterFactory = new DatabaseRouterFactory(config);
				logger.info("pasered config.");


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
