package fengfei.forest.slice.config;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.Router;

public interface RouterFactory {

	void config(Config config);

	<Key> Router<Key> getRouter(String routerName);

	<Key> Router<Key> getRouter(Equalizer<Key> equalizer, String routerName);

}