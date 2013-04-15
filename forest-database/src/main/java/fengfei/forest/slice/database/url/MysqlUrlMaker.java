package fengfei.forest.slice.database.url;

import fengfei.forest.slice.database.DatabaseResource;
import fengfei.forest.slice.database.UrlMaker;

public class MysqlUrlMaker implements UrlMaker {

	@Override
	public String makeUrl(DatabaseResource resource) {

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