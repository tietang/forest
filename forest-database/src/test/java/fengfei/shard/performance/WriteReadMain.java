package fengfei.shard.performance;

import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.SliceConfigReader;
import fengfei.forest.slice.config.xml.XmlSliceConfigReader;
import fengfei.forest.slice.database.DatabaseRouterFactory;
import fengfei.forest.slice.database.utils.Transactions;

public class WriteReadMain implements Runnable {

    // for test
    private static long sleepTime = 10;
    private static int startWriteNum = 0;
    private static int threads = 10;

    public static void main(String args[]) throws Exception {
        Count.start(1);
        Count.setMaxRowNums(1000);
        // 0=sleep time,1=thread num

        if (args.length >= 1) {
            sleepTime = Long.parseLong(args[0]);
        }
        if (args.length >= 2) {
            threads = Integer.parseInt(args[1]);
        }

        System.out.println("");
        System.out.println("request sleep time(ms): " + sleepTime);
        System.out.println("request thread num: " + threads);

        System.out.println("");
        // install
        SliceConfigReader configReader = new XmlSliceConfigReader("cp:config_test.xml");
        Config config = configReader.read("/root");
        // System.out.println(config.toString());
        DatabaseRouterFactory databaseRouterFactory = new DatabaseRouterFactory(config);
        Transactions.setDatabaseSliceGroupFactory(databaseRouterFactory);
        //
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(new WriteReadMain(), "WriteReadTest-" + i);
        }
        for (int j = 0; j < ts.length; j++) {
            ts[j].start();
            System.out.println("start thread:" + ts[j].getName());
        }
    }

    @Override
    public void run() {
        Count.setStartWriteNum(startWriteNum);

        try {
            final WriteReadService writeRead = new WriteReadService();

            while (true) {

                try {
                    writeRead.write();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
