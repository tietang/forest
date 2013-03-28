package fengfei.forest.slice.database;

public class PostgreSQLConnectonUrlMaker implements ConnectonUrlMaker {

    @Override
    public String makeUrl(DatabaseResource  resource) {
        String url = "jdbc:postgresql://%s:%s/%s";
        return String.format(url, resource.getHost(), resource.getPort(), resource.getSchema());
    }

}