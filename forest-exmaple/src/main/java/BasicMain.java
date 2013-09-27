import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.SliceConfigReader;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;
import fengfei.forest.slice.config.zk.ZKSliceConfigReader;
import fengfei.forest.slice.database.DatabaseRouterFactory;
import fengfei.forest.slice.database.utils.Transactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class BasicMain {
    static Logger logger = LoggerFactory.getLogger(BasicMain.class);

    public static void main(String[] args) {
        DatabaseRouterFactory databaseRouterFactory;
        SliceConfigReader configReader;

        String sliceConfig = "file/cp:config2.xml";
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
                Transactions.setDatabaseSliceGroupFactory(databaseRouterFactory);
                logger.info("pasered config.");
                final int id = 1;
                boolean created = Transactions.execute(
                        "Sequence",
                        new Long(id),
                        SliceResource.Function.Write,
                        new Transactions.TaCallback<Boolean>() {

                            @Override
                            public Boolean execute(ForestGrower grower, String suffix) throws SQLException {
                                System.out.println(id + " " + suffix);
                                suffix = String.valueOf(id % 2 + 1);
                                System.out.println(id + " " + suffix);
                                return true;
                            }

                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
