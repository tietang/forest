package fengfei.forest.database.dbutils;

import java.sql.SQLException;


public interface GrowerCallback<T> {

    T execute(ForestGrower evaluator) throws SQLException;
}
