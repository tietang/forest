package fengfei.shard.performance;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.database.DataAccessException;
import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.database.utils.Transactions;
import fengfei.forest.slice.database.utils.Transactions.TaCallback;

public class WriteReadService {

    static Logger logger = LoggerFactory.getLogger("CLient");
    protected static Random rand = new Random(System.currentTimeMillis());
    protected long totalSetupTime;
    public AtomicInteger ct = new AtomicInteger(1);

    int port = 10000;
    public AtomicInteger sidGenerator = new AtomicInteger(1);

    public WriteReadService() {
        super();

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
        int sid = 30000000 + Math.abs(random.nextInt(40000000));
        int did = sid + 100;
        String type = "X";
        writeSingle(sid, did, type);
        // System.out.println(result.getResult().size());
        Count.incrementWriteNum();
        Count.increment();
    }

    public void read() throws Exception {

        int countNum = ct.getAndIncrement();

        // System.out.println(result.getResult().size());
        Count.incrementReadNum();
        Count.increment();
    }

    final static String UnitName = "profile";

    private void writeSingle(final long sid, final long did, final String type) throws Exception {
        try {

            Integer up = Transactions.execute(
                UnitName,
                sid,
                Function.Read,
                new TaCallback<Integer>() {

                    @Override
                    public Integer execute(ForestGrower grower, String suffix) throws SQLException {
                        String sql = "SELECT source_id, destination_id, edge_type, state, created_at, updated_at, valid_time, expired_at,created_at  FROM forward%s_edges WHERE source_id = ? AND destination_id = ? AND edge_type= ? for update";
                        Map<String, Object> map = grower.selectOne(
                            String.format(sql, suffix),
                            sid,
                            did,
                            type);
                        long update_at = System.currentTimeMillis();
                        int updated = 0;
                        if (map == null) {
                            String insert = "INSERT INTO forward%s_edges (source_id., updated_at, destination_id, state, edge_type, valid_time, expired_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                            updated = grower.update(
                                String.format(insert, suffix),
                                sid,
                                update_at,
                                did,
                                0,
                                type,
                                60000,
                                60000,
                                update_at / 1000);

                        } else {
                            String update = "UPDATE forward%s_edges SET updated_at = ? "
                                    + "WHERE source_id = ? AND edge_type = ?  AND destination_id. = ? ";
                            updated = grower.update(
                                String.format(update, suffix),
                                update_at,
                                sid,
                                type,
                                did

                            );

                        }
                        return updated;
                    }

                });
        } catch (Exception e) {
            throw new DataAccessException("write  error.", e);
        }
    }

}
