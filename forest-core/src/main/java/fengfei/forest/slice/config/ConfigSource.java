package fengfei.forest.slice.config;

import java.util.List;
import java.util.Map;

import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.BerainWatchedEvent;

public interface ConfigSource {
    public final static String ValueKey="_value";

    String getNamespace();

    List<BerainEntry> children(String parentPath) throws Exception;

    List<String> listChildrenPath(String parentPath) throws Exception;

    Map<String ,Map<String, String>> listChildren(String parentPath) throws Exception;

    String get(String path) throws Exception;

    BerainEntry getFull(String path) throws Exception;

    boolean exists(String path) throws Exception;

    void addMonitor(String path, Monitor monitor) throws Exception;

    public static interface Monitor {

        void onChildrenChanged(BerainWatchedEvent event);

        void onDataChanged(BerainWatchedEvent event);
    }
}
