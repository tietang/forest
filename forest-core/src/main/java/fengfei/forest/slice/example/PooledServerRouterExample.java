package fengfei.forest.slice.example;

import fengfei.forest.slice.Detector;
import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.server.pool.CommonsPoolableObjectFactory;
import fengfei.forest.slice.server.pool.CommonsPoolableSourceFactory;
import fengfei.forest.slice.server.pool.PoolableException;
import fengfei.forest.slice.server.pool.PoolableServerRouter;
import fengfei.forest.slice.server.pool.PooledSource;

public class PooledServerRouterExample extends BaseRouterExample {

	/**
	 * @param args
	 * @throws PoolableException
	 */
	public static void main(String[] args) throws PoolableException {
		Detector detector = new Detector() {

			@Override
			public boolean detect(SliceResource resource) {
				try {
					Clientx x=	commonsPoolableObjectFactory.makeObject();
					return commonsPoolableObjectFactory.validateObject(x);
				} catch (Exception e) {
					return false;
				}
			}
		};
		AccuracyRouter<User> faced = new AccuracyRouter<>(equalizer);
		PoolableServerRouter<User, Clientx> router = new PoolableServerRouter<>(
				faced, commonsPoolableSourceFactory);
		setupGroup(router);
		faced.setDetector(detector);
	
		router.setSelectType(SelectType.Hash);
		router.setOverflowType(OverflowType.Last);
		// //log.info(router);
		//
		PooledSource<Clientx> res = router.locate(new User(1l));
		Clientx x = res.getSource();
		log.info(x + ": " + x.ping());
		//
		res = router.locate(new User(1l), Function.Read);
		x = res.getSource();
		log.info(x + ": " + x.ping());
		//
		res = router.locate(new User(1l), Function.Write);
		x = res.getSource();
		log.info(x + ": " + x.ping());

	}

	static CommonsPoolableObjectFactory<Clientx> commonsPoolableObjectFactory = new CommonsPoolableObjectFactory<BaseRouterExample.Clientx>() {

		@Override
		public Clientx makeObject() throws Exception {
			return new Clientx(host, port);
		}

		@Override
		public void destroyObject(Clientx obj) throws Exception {
			obj.close();
		}

		@Override
		public boolean validateObject(Clientx obj) {
			try {
				return "pong".equals(obj.ping());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void activateObject(Clientx obj) throws Exception {
		}

		@Override
		public void passivateObject(Clientx obj) throws Exception {
		}
	};
	static CommonsPoolableSourceFactory<Clientx> commonsPoolableSourceFactory = new CommonsPoolableSourceFactory<>(
			commonsPoolableObjectFactory);
}
