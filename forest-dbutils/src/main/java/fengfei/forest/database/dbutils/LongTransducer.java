package fengfei.forest.database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongTransducer implements Transducer<Long> {

    @Override
    public Long transform(ResultSet rs) throws SQLException {
        return rs.getLong(1);
    }

}
