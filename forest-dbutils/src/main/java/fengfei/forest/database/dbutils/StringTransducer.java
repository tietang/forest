package fengfei.forest.database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTransducer implements Transducer<String> {

    @Override
    public String transform(ResultSet rs) throws SQLException {
        return rs.getString(1);
    }

}
