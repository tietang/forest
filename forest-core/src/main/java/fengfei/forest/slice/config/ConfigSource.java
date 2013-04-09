package fengfei.forest.slice.config;

import java.util.List;

import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.BerainWatchedEvent;

public interface ConfigSource {
	public String getNamespace();

	List<BerainEntry> children(String parentPath) throws Exception;

	List<String> listChildren(String parentPath) throws Exception;

	String get(String path) throws Exception;

	BerainEntry getFull(String path) throws Exception;

	boolean exists(String path) throws Exception;

	void addMonitor(String path, Monitor monitor) throws Exception;

	public static interface Monitor {
		void onChildrenChanged(BerainWatchedEvent event);

		void onDataChanged(BerainWatchedEvent event);
	}
}
