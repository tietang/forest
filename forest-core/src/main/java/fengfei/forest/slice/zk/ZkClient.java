package fengfei.forest.slice.zk;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryNTimes;

public class ZkClient {
	public static final String ROOT_PATH = "/";
	public static final String SEPARATOR = "/";
	private CuratorFramework client;

	public ZkClient(Properties properties) {
		String host = properties.getProperty("zk.host");
		String namespace = properties.getProperty("zk.namespace");
		initNamespace();
		int timeout = 5000;
		int retryTimes = Integer.MAX_VALUE;
		int sleepRetry = 1000;

		String stimeout = properties.getProperty("zk.connectionTimeoutMs");
		String sretryTimes = properties.getProperty("zk.RetryNTimes");
		String ssleepRetry = properties.getProperty("zk.sleepMsBetweenRetries");

		if (null == namespace || "".equals(namespace)) {
			namespace = "forest";
		}
		if (null != stimeout && !"".equals(stimeout)) {
			timeout = Integer.parseInt(stimeout);
		}
		if (null != sretryTimes && !"".equals(sretryTimes)) {
			retryTimes = Integer.parseInt(sretryTimes);
		}
		if (null != ssleepRetry && !"".equals(ssleepRetry)) {
			sleepRetry = Integer.parseInt(ssleepRetry);
		}
		client = CuratorFrameworkFactory.builder().connectString(host)
				.namespace(namespace)
				.retryPolicy(new RetryNTimes(retryTimes, sleepRetry))
				.connectionTimeoutMs(timeout).build();

		client.start();
		initNamespace();

	}

	public static void main(String[] args) throws Exception {

		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString("localhost:2181").namespace("test")
				.retryPolicy(new RetryNTimes(100, 1222))
				.connectionTimeoutMs(12222).build();
		System.out.println("getNamespace: " + client.getNamespace());
		client.start();

		client.inTransaction()
				.create()
				.forPath("/k22" + System.currentTimeMillis() / 1000,
						"v1".getBytes()).and().commit();
	}

	public ZkClient(CuratorFramework client) {
		super();
		this.client = client;
		initNamespace();
	}

	public CuratorFramework getCuratorFramework() {
		return client;
	}

	public void initNamespace() {
		PathUtils.namespace = "/" + client.getNamespace();
	}

	public RainModel addNode(String pid, String key, String value)
			throws Exception {
		String parentPath = PathUtils.id2path(pid);

		String path = parentPath + SEPARATOR + key;
		System.out.println("-path----------------------------:  " + path);
		client.inTransaction().create().forPath(path, value.getBytes()).and()
				.commit();
		parentPath = "".equals(parentPath) ? ROOT_PATH : parentPath;
		RainModel model = new RainModel();
		model.key = key;
		model.path = path;
		model.value = value;
		System.out.println("model:  " + model);
		return model;
	}

	public RainModel editNode(String id, String value) throws Exception {

		String path = PathUtils.id2path(id);
		String key = PathUtils.getKey(path);
		client.inTransaction().setData().forPath(path, value.getBytes()).and()
				.commit();

		RainModel model = new RainModel();
		model.key = key;
		model.path = path;
		model.value = value;
		return model;
	}

	public RainModel removeNode(String id) throws Exception {
		String path = PathUtils.id2path(id);
		String key = PathUtils.getKey(path);
		byte[] data = client.getData().forPath(path);
		client.inTransaction().delete().forPath(path).and().commit();
		RainModel model = new RainModel();
		model.key = key;
		model.path = path;
		model.value = new String(data);
		return model;
	}

	public RainModel getNodeByPath(String path) throws Exception {
		String key = PathUtils.getKey(path);
		byte[] data = client.getData().forPath(path);
		RainModel model = new RainModel();
		model.key = key;
		model.path = path;
		model.value = new String(data);
		return model;
	}

	public RainModel getNode(String id) throws Exception {
		// System.out.println("===========================================");
		// System.out.println(id);
		String path = PathUtils.id2path(id);
		String tmpPath = path;

		if (path.endsWith("/")) {
			tmpPath = path.substring(0, path.length() - 1);
		}
		if ("".equals(path)) {
			path = "/";
		}
		String key = PathUtils.getKey(tmpPath);
		byte[] data = client.getData().forPath(tmpPath);
		RainModel model = new RainModel();
		model.key = key;
		model.path = tmpPath;
		model.value = new String(data);
		return model;

	}

	public boolean exists(String path) throws Exception {
		return client.checkExists().forPath(path) != null;

	}

	public List<RainModel> nextNodes(String id) throws Exception {
		List<RainModel> models = new ArrayList<>();
		System.out.println("===========================================");
		System.out.println(id);
		String path = PathUtils.id2path(id);
		String tmpPath = path;

		if (path.endsWith("/")) {
			tmpPath = path.substring(0, path.length() - 1);
		}
		if ("".equals(path)) {
			path = "/";
		}
		System.out.printf("%s %s %s \n", id, path, tmpPath);
		if (client.checkExists().forPath(tmpPath) != null) {
			List<String> paths = client.getChildren().forPath(tmpPath);
			for (String cpath : paths) {
				String ppath = tmpPath + SEPARATOR + cpath;
				byte[] data = client.getData().forPath(ppath);
				RainModel model = new RainModel();
				model.key = PathUtils.getKey(cpath);
				model.path = ppath;
				model.value = new String(data);
				models.add(model);
			}
		}
		System.out.println(models);
		return models;
	}

}
