package fengfei.forest.slice.config.zk;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.zk.ZkBerainClient;
import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.RouterFactory;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.utils.ResourcesUtils;

public class ZKRouterFactory implements RouterFactory {
	static final String FOREST_ROOT_PATH = "/router";
	static final String FOREST_RESOURCES_PATH = FOREST_ROOT_PATH + "/resources";
	static final String FOREST_EXTRA_INFO_PATH = FOREST_RESOURCES_PATH
			+ "/extra_infos";
	static final String FOREST_SLICES_PATH = FOREST_ROOT_PATH + "/slices";
	static Logger log = LoggerFactory.getLogger(ZKRouterFactory.class);
	ZkBerainClient client;
	String forest_path;

	public ZKRouterFactory() {

		try {
			Properties properties = ResourcesUtils
					.getResourceAsProperties("zk.properties");
			client = new ZkBerainClient(properties);
		} catch (IOException e) {
			log.error("init error.", e);
		}

	}

	/**
	 * <pre>
	 *  -router
	 *  	- resources
	 *  		- name=weight (192.168.1.11:9080:db1=1)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 *  		- name=weight (192.168.1.12:9080:db1=1)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 *  		- name=weight (192.168.1.13:9080:db1=1)
	 *  			-host=192.168.1.11
	 *  			-port=9080
	 *  			-user=root
	 *  			-password=pwd
	 * 
	 * 
	 * </pre>
	 * 
	 * @throws Exception
	 */
	<Key> void readResources() throws Exception {
		Router<Key> router = new AccuracyRouter<>();
		//
		List<String> reses = client.getCuratorFramework().getChildren()
				.forPath(FOREST_RESOURCES_PATH);
		Map<String, String> extraInfo = readExtraInfos();
		for (String resPath : reses) {
			BerainEntry res = client.getFull(resPath);
			String name = res.getKey();
			Resource resource = new Resource(name);
			List<BerainEntry> entries = client.nextChildren(resPath);
			Map<String, String> kv = toMap(entries);
			// 1.put top extraInfo
			resource.addExtraInfo(extraInfo);
			// 2.put name/value split info
			splitResourceNameValue(resource, res);
			// 3. put key/value info
			resource.addExtraInfo(kv);
			router.register(resource);

		}
	}

	void splitResourceNameValue(Resource resource, BerainEntry entry) {
		String name = entry.getKey();
		if (name.contains(":")) {
			String[] ns = name.split(":");
			if (ns.length > 1) {
				resource.addExtraInfo("host", ns[0]);
				resource.addExtraInfo("port", ns[1]);
			}
			if (ns.length > 2) {
				resource.addExtraInfo("schema", ns[2]);
			}
		}
		int weight=entry.intValue();
		resource.setWeight(weight);
	}

	Map<String, String> toMap(List<BerainEntry> entries) {
		Map<String, String> map = new HashMap<>();
		for (BerainEntry entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	Map<String, String> readExtraInfos() throws Exception {
		List<BerainEntry> entries = client.nextChildren(FOREST_EXTRA_INFO_PATH);
		return toMap(entries);
	}

	@Override
	public void config(Config config) {

	}

	@Override
	public <Key> Router<Key> getRouter(String routerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <Key> Router<Key> getRouter(Equalizer<Key> equalizer,
			String routerName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
