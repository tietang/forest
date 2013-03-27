package fengfei.forest.database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractListHandler;

public class ListHandler<T> extends AbstractListHandler<T> {

    private Transducer<T> converter;

    public ListHandler(Transducer<T> converter) {
        this.converter = converter;
    }

    @Override
    protected T handleRow(ResultSet rs) throws SQLException {
        return converter.transform(rs);
    }
}
