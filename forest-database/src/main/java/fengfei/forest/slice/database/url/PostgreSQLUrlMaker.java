package fengfei.forest.slice.database.url;

import fengfei.forest.slice.database.DatabaseResource;
import fengfei.forest.slice.database.UrlMaker;

public class PostgreSQLUrlMaker implements UrlMaker {

    @Override
    public String makeUrl(DatabaseResource  resource) {
        String url = "jdbc:postgresql://%s:%s/%s";
        return String.format(url, resource.getHost(), resource.getPort(), resource.getSchema());
    }

}