package fengfei.forest.slice.example;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class BaseRouterExample {
	public static Logger log = LoggerFactory.getLogger("Example");

	protected static void setupGroup(Router<User> router) {
		int ip = 2;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);
				SliceResource sliceResource = new SliceResource(resource);
				sliceResource.setFunction(j == 0 ? Function.Write
						: Function.Read);
				sliceResource.addParams(extraInfo(ip));
				router.register(Long.valueOf(i), String.valueOf(i),
						sliceResource);
			}
		}
	}

	protected static Map<String, String> extraInfo(int ip) {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("host", "192.168.1." + ip);
		extraInfo.put("port", "8002");
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		return extraInfo;
	}

	public static class User {
		long uid;
		String name;
		String email;
		int age;
		Date birthday;

		public User(long uid) {
			super();
			this.uid = uid;
		}

		public User(long uid, String name, String email, int age, Date birthday) {
			super();
			this.uid = uid;
			this.name = name;
			this.email = email;
			this.age = age;
			this.birthday = birthday;
		}

		public long getUid() {
			return uid;
		}

		public void setUid(long uid) {
			this.uid = uid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Date getBirthday() {
			return birthday;
		}

		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
	}

	static Equalizer<User> equalizer = new Equalizer<BaseRouterExample.User>() {

		@Override
		public long get(User key, int sliceSize) {
			long id = key.getUid();
			return id % sliceSize;
		}
	};

	public static class Clientx {
		String host;
		int port;

		public Clientx(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}

		public void connect() {
			// to connect to server
			
			log.info(String.format("Connected host: %s:%d", host,
					port));
		}

		public void close() {
			// close connection
			log.info(String.format(
					"Closed connection for host:  %s:%d", host, port));
		}

		public String ping() {
			return "pong";
		}

		@Override
		public String toString() {
			return "Clientx [host=" + host + ", port=" + port + "]";
		}

	}

}
