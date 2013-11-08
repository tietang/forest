package fengfei.forest.slice.config;

import java.util.HashMap;
import java.util.Map;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.exception.NonExistedSliceException;

public abstract class AbstractRouterFactory<R extends SliceResource>  implements
		RouterFactory<R> {

	protected Map<String, Router<?, ?>> routers = new HashMap<>();

	public AbstractRouterFactory() {

	}

	public AbstractRouterFactory(Config config) {
		config(config);
	}

	@SuppressWarnings("unchecked")
	protected <T> T newInstance(String className) {
		try {
			return (T) Class.forName(className.trim()).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			throw new IllegalArgumentException("non-exist class: " + className);
		}
	}

	public <Key> Router<Key, R> getRouter(String routerName) {
		@SuppressWarnings("unchecked")
		Router<Key, R> router = (Router<Key, R>) routers.get(routerName);
		if (router == null) {
			throw new NonExistedSliceException("routerName=" + routerName);
		}
		return router;
	}

	public <Key> Router<Key, R> getRouter(Equalizer<Key> equalizer,
			String routerName) {
		Router<Key, R> router = getRouter(routerName);
		if (equalizer != null) {
			router.setEqualizer(equalizer);
		}
		return router;
	}

	@Override
	public <Key> Router<Key, R> getRouter(Equalizer<Key> equalizer,
			Plotter plotter, String routerName) {
		Router<Key, R> router = getRouter(equalizer, routerName);
		if (plotter != null) {
			router.setPlotter(plotter);
		}
		return router;
	}
}
