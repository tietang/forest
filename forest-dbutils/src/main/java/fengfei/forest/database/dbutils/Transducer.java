package fengfei.forest.database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Transducer<T> {

    T transform(ResultSet rs) throws SQLException;
}
