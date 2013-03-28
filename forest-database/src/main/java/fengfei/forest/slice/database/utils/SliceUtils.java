package fengfei.forest.slice.database.utils;

import fengfei.forest.slice.database.DatabaseResource;

public class SliceUtils {

	public static String getValidationQuery(DatabaseResource resource) {
		String query = resource.getExtraInfo().get("validationQuery");
		if (query == null)
			query = "select 1";
		return query;
	}

	public static boolean getDefaultBoolean(DatabaseResource resource, String name) {
		return getDefaultBoolean(resource, name, true);
	}

	public static boolean getDefaultBoolean(DatabaseResource resource,
			String name, boolean def) {
		boolean b = def;
		String bString = resource.getExtraInfo().get(name);
		if (bString != null) {
			b = Boolean.parseBoolean(bString);
		}
		return b;
	}

	public static int getDefaultInt(DatabaseResource resource, String name,
			int def) {
		int b = def;
		String bString = resource.getExtraInfo().get(name);
		if (bString != null) {
			b = Integer.parseInt(bString);
		}
		return b;
	}

}
