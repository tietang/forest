package fengfei.forest.slice.config.xml;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.forest.slice.Range;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.ConfigSource;
import fengfei.forest.slice.config.SliceConfigReader;
import fengfei.forest.slice.config.Config.RouterConfig;
import fengfei.forest.slice.config.Config.SliceConfig;
import fengfei.forest.slice.utils.MapUtils;

public class XmlSliceConfigReader extends SliceConfigReader {

    public static final String SliceIdKey = "id";
    static Logger log = LoggerFactory.getLogger(XmlSliceConfigReader.class);

    public XmlSliceConfigReader(String xmlFile) {
        super();
        source = new XmlConfigSource(xmlFile);
        namespace = source.getNamespace();
    }

    public XmlSliceConfigReader(InputStream in) {
        source = new XmlConfigSource(in);
        namespace = source.getNamespace();
    }

    protected long getSliceId(BerainEntry sliceEntry, Map<String, String> children) {

        return Long.parseLong(children.get(SliceIdKey));
    }

    public Set<SliceConfig> readSlices(String slicePath) throws Exception {
        check(slicePath);
        Set<SliceConfig> sliceConfigs = new HashSet<>();
        log.info(String.format("read slices path: namespace=%s, path=%s", namespace, slicePath));
        // List<String> slicePaths = source.listChildrenPath(slicePath);
        Map<String, Map<String, String>> listChildren = source.listChildren(slicePath);
        Set<Entry<String, Map<String, String>>> childrenSets = listChildren.entrySet();
        for (Entry<String, Map<String, String>> entry : childrenSets) {
            String path = entry.getKey();
            Map<String, String> value = entry.getValue();
            SliceConfig sliceConfig = readSliceConfig(path, value);
            sliceConfigs.add(sliceConfig);

        }
        return sliceConfigs;
    }

    public SliceConfig readSliceConfig(String path, Map<String, String> entries) throws Exception {

        Map<String, String> kv = splitValue(entries.get(ConfigSource.ValueKey));
        Long sliceId = MapUtils.getLong(entries, "id");
        kv.putAll(entries);

        String alias = MapUtils.getString(kv, S_ALIAS, String.valueOf(sliceId));
        String read = kv.get(S_READ);
        String write = kv.get(S_WRITE);
        String rw = kv.get(S_READ_WRITE);
        String sourceKey = kv.get(S_RANGE);
        String subRouterId = kv.get(S_SUB_ROUTER_ID);
        String subRouterPath = kv.get(S_SUB_ROUTER_PATH);
        Range[] ranges = toRanges(sourceKey);
        SliceConfig sliceConfig = new SliceConfig();
        sliceConfig.path = path;
        sliceConfig.id = sliceId;
        sliceConfig.alias = alias;
        sliceConfig.readRes = read;
        sliceConfig.writeRes = write;
        sliceConfig.readWriteRes = rw;

        sliceConfig.sourceKey = sourceKey;
        sliceConfig.ranges = ranges;
        sliceConfig.extraInfo = kv;
        sliceConfig.subRouterId = subRouterId;
        sliceConfig.subRouterPath = subRouterPath;
        if (subRouterPath != null && !"".equals(subRouterPath)) {
            BerainEntry routerEntry = source.getFull(subRouterPath);
            RouterConfig routerConfig = readRouterConfig(routerEntry);
            sliceConfig.subRouter.add(routerConfig);
        }
        String subSlice = kv.get(S_SUB_SLICES_PATH.substring(1));
        if (subSlice != null && !"".equals(subSlice)) {
            SliceConfig subSliceConfig = readSliceConfig(path + S_SUB_SLICES_PATH);
            sliceConfig.subSlices.add(subSliceConfig);
        }
        return sliceConfig;

    }

    public static void main(String[] args) {
        XmlSliceConfigReader reader = new XmlSliceConfigReader("cp:config.xml");

        Config config = reader.read("/root/main");
        System.out.println(config);
    }

}
