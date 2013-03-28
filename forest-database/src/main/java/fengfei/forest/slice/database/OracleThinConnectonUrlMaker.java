package fengfei.forest.slice.database;

public class OracleThinConnectonUrlMaker implements ConnectonUrlMaker {

	@Override
	public String makeUrl(DatabaseResource resource) {
		String url = "jdbc:oracle:thin:@%s:%s:%s";
		return String.format(url, resource.getHost(), resource.getPort(),
				resource.getSchema());
	}

}