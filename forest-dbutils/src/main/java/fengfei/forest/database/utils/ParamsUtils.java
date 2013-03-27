package fengfei.forest.database.utils;

import java.util.Map;

public class ParamsUtils {

	public static String getValidationQuery(Map<String, String> params) {
		String query = params.get("validationQuery");
		if (query == null)
			query = "select 1";
		return query;
	}

	public static boolean getDefaultBoolean(Map<String, String> params, String name) {
		return getDefaultBoolean(params, name, true);
	}

	public static boolean getDefaultBoolean(
			Map<String, String> params,
			String name,
			boolean def) {
		boolean b = def;
		String bString = params.get(name);
		if (bString != null) {
			b = Boolean.parseBoolean(bString);
		}
		return b;
	}

	public static int getDefaultInt(Map<String, String> params, String name, int def) {
		int b = def;
		String bString = params.get(name);
		if (bString != null) {
			b = Integer.parseInt(bString);
		}
		return b;
	}

}
