package fengfei.forest.database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class OneBeanHandler<T> implements ResultSetHandler<T> {

    private Transducer<T> transducer;

    public OneBeanHandler(Transducer<T> converter) {
        this.transducer = converter;
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return transducer.transform(rs);
        } else {
            return null;
        }
    }
}
