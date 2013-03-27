package fengfei.forest.slice.database;

public class MysqlConnectonUrlMaker implements ConnectonUrlMaker {

	@Override
	public String makeUrl(ServerResource resource) {

		String characterEncoding = resource.getExtraInfo().get(
				"characterEncoding");

		if (null == characterEncoding || "".equals(characterEncoding)) {
			String url = "jdbc:mysql://%s:%s/%s";
			return String.format(url, resource.getHost(), resource.getPort(),
					resource.getSchema());
		} else {
			String url = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s";
			return String.format(url, resource.getHost(), resource.getPort(),
					resource.getSchema(), characterEncoding);
		}

	}
}