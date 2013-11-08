package fengfei.forest.slice.config;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.SliceResource;

public interface RouterFactory<R extends SliceResource> {

	void config(Config config);

	<Key> Router<Key, R> getRouter(String routerName);

	<Key> Router<Key, R> getRouter(Equalizer<Key> equalizer, String routerName);

	<Key> Router<Key, R> getRouter(Equalizer<Key> equalizer, Plotter plotter,
			String routerName);

}