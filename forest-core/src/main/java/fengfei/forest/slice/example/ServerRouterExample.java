package fengfei.forest.slice.example;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.plotter.HashPlotter;
import fengfei.forest.slice.server.ServerResource;
import fengfei.forest.slice.server.ServerRouter;
import fengfei.forest.slice.server.pool.PoolableException;

public class ServerRouterExample extends BaseRouterExample {

	/**
	 * @param args
	 * @throws PoolableException
	 */
	public static void main(String[] args) throws PoolableException {

		AccuracyRouter<User> faced = new AccuracyRouter<>(equalizer);
		ServerRouter<User> router = new ServerRouter<>(faced);
		setupGroup(router);
		router.setPlotter(new HashPlotter());
		router.setOverflowType(OverflowType.Last);
		// //System.out.println(router);
		//
		ServerResource res = router.locate(new User(1l));
		Clientx x = new Clientx(res.getHost(), res.getPort());
		x.connect();
		log.info(x + ": " + x.ping());
		x.close();
		//
		res = router.locate(new User(1l), Function.Read);
		x = new Clientx(res.getHost(), res.getPort());
		x.connect();
		log.info(x + ": " + x.ping());
		x.close();
		//
		res = router.locate(new User(1l), Function.Write);
		x = new Clientx(res.getHost(), res.getPort());
		x.connect();
		log.info(x + ": " + x.ping());
		x.close();

	}

}
