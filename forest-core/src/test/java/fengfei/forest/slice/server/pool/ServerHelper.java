package fengfei.forest.slice.server.pool;

import java.net.UnknownHostException;

import org.msgpack.rpc.Client;
import org.msgpack.rpc.Server;
import org.msgpack.rpc.loop.EventLoop;

public class ServerHelper {

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread() {

			public void run() {
				Serverx serverx = new Serverx();
				try {
					serverx.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		t.start();
		Thread t2 = new Thread() {

			public void run() {
				Clientx clientx = new Clientx("localhost", 1980);
				RPCInterface x = clientx.getIface();
				System.out.println(x.ping());
				System.out.println(x.hello("tietang"));
			};
		};
		t2.start();
		t.join();
		t2.join();
	}

	public static class Serverx {

		EventLoop loop;
		Server svr;

		public void start() throws Exception {
			loop = EventLoop.defaultEventLoop();
			svr = new Server(loop);
			svr.serve(new RPCInterfaceImpl());
			svr.listen(1980);
			loop.join();
		}

		public void close() {
			svr.close();
			loop.shutdown();
		}
	}

	public static class Clientx {

		RPCInterface iface;
		Client client;
		EventLoop loop;

		public Clientx(String host, int port) {
			loop = EventLoop.defaultEventLoop();
			try {
				client = new Client(host, port, loop);
				iface = client.proxy(RPCInterface.class);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		public String ping() {
			return iface.ping();
		}

		public RPCInterface getIface() {
			return iface;
		}

		public void close() {
			client.close();
			loop.shutdown();
		}
	}

	public static interface RPCInterface {

		String ping();

		String hello(String name);
	}

	public static class RPCInterfaceImpl implements RPCInterface {

		@Override
		public String ping() {
			return "pong";
		}

		@Override
		public String hello(String name) {
			return "hello, " + name;
		}
	}
}
