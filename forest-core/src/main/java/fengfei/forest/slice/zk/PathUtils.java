package fengfei.forest.slice.zk;


public class PathUtils {

	public static String namespace;

	public static String getKey(String path) {
		String[] ps = path.split("/");
		return ps[ps.length - 1];
	}

	public static String getParent(String path) {
		int index = path.lastIndexOf('/');
		return path.substring(0, index);
	}

	public static String toGlobalPath(String path) {
		return removeLastSlash(namespace + path);
	}

	public static String toPath(String globalpath) {
		return removeLastSlash(globalpath.replace(namespace, ""));
	}

	public static String removeLastSlash(String path) {
		if (!"/".equals(path) && path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}

	}

	public static void main(String[] args) {
		String path = "/r34234/";
		System.out.println(removeLastSlash(path));
	}

	public static String id2path(String id) {
		String path = id.replaceAll("[_]", "/");
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		System.out.println("-----------------------------:  " + path);
		return path;
	}
}
