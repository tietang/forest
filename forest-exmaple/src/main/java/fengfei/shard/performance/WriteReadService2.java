package fengfei.shard.performance;

import fengfei.exmaple.Stats;
import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.database.utils.Transactions;
import fengfei.forest.slice.database.utils.Transactions.TaCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class WriteReadService2 {

    static Logger logger = LoggerFactory.getLogger("CLient");
    protected static Random rand = new Random(System.currentTimeMillis());
    protected long totalSetupTime;
    public AtomicInteger ct = new AtomicInteger(1);

    int port = 10000;
    public AtomicInteger sidGenerator = new AtomicInteger(1);
    private int start = 1;
    private int size = 30000000;

    public WriteReadService2(int start, int size) {
        super();
        this.start = start;
        this.size = size;
    }

    public static int random(int minNum, int maxNum) {
        int r = random.nextInt() % maxNum;
        if (r > maxNum || r < minNum) {
            r = random(minNum, maxNum);
        }
        return Math.abs(r);
    }

    protected static Random random = new Random();

    public void write() throws Exception {

        int countNum = ct.getAndIncrement();
        int sid = start + random(1, size);
        writeSingle(sid);
        // System.out.println(result.getResult().size());
        Count.incrementWriteNum();
        Count.increment();
    }

    public void read() throws Exception {

        int countNum = ct.getAndIncrement();
        int sid = start + random(1, size);
        readSingle(sid);
        // System.out.println(result.getResult().size());
        Count.incrementReadNum();
        Count.increment();
    }

    final static String UnitName = "user";

    private void readSingle(final long sid) throws Exception {
        try {

            Integer up = Transactions.execute(
                UnitName,
                sid,
                Function.Read,
                new TaCallback<Integer>() {

                    @Override
                    public Integer execute(ForestGrower grower, String suffix) throws SQLException {
                        suffix = "";
                        String sql = "SELECT id_user, email, username FROM user" + suffix
                                + " where id_user=?";
                        Map<String, Object> map = grower.selectOne(sql, sid);

                        Stats.incr("read");
                        return 1;
                    }

                });
        } catch (Exception e) {
            Stats.incr("read_error");
            throw e;
        }
    }

    private void writeSingle(final long sid) throws Exception {
        try {

            Integer up = Transactions.execute(
                UnitName,
                sid,
                Function.Read,
                new TaCallback<Integer>() {

                    @Override
                    public Integer execute(ForestGrower grower, String suffix) throws SQLException {
                        suffix = "";
                        String sql = "SELECT id_user, email, username FROM user" + suffix
                                + " where id_user=?";
                        Map<String, Object> map = grower.selectOne(sql, sid);
                        long updateAt = System.currentTimeMillis();
                        long createAt = updateAt / 1000;
                        int updated = 0;
                        if (map == null) {
                            String insert = "INSERT INTO user"
                                    + suffix
                                    + " ( email, username, password,create_at,update_at) VALUES ( ?,?,?,?,?)";

                            updated = grower.update(
                                insert,
                                sid + "xxx@ccc.com",
                                "username" + sid,
                                "123456" + sid,
                                createAt,
                                updateAt);

                        } else {
                            String update = "update user" + suffix
                                    + " set password=?,update_at=?   where   id_user=?";
                            updated = grower.update(update, "newpassword" + sid, updateAt, sid);
                        }

                        return updated;
                    }

                });
            if (up > 0) {
                Stats.incr("create_sucess");
            } else {
                Stats.incr("create_fail");
            }
        } catch (Exception e) {
            Stats.incr("create_error");
            throw e;

        }
        Stats.incr("create");
    }

}
