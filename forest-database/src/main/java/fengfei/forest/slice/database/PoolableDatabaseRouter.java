package fengfei.forest.slice.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import fengfei.forest.slice.SliceResourceGroup;
import fengfei.forest.slice.server.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.database.pool.PoolableDataSourceFactory;
import fengfei.forest.database.pool.PoolableException;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.SliceRuntimeException;
import fengfei.forest.slice.server.AbstractDecorateRouter;

public class PoolableDatabaseRouter<Key> extends
        AbstractDecorateRouter<Key, PoolableDatabaseResource> {

    static final Logger log = LoggerFactory
            .getLogger(PoolableDatabaseRouter.class);
    public static Map<String, DataSource> pooledDataSources = new ConcurrentHashMap<>();
    private UrlMaker urlMaker;
    private PoolableDataSourceFactory poolableDataSourceFactory;

    public PoolableDatabaseRouter(Router<Key, SliceResource> router,
                                  UrlMaker urlMaker,
                                  PoolableDataSourceFactory poolableDataSourceFactory) {
        super(router);
        this.urlMaker = urlMaker;
        this.poolableDataSourceFactory = poolableDataSourceFactory;
    }

    public PoolableDatabaseRouter(Router<Key, SliceResource> router,
                                  PoolableDataSourceFactory poolableDataSourceFactory) {
        super(router);
        this.poolableDataSourceFactory = poolableDataSourceFactory;
    }

    public PoolableDatabaseResource locate(Key key) {

        PoolableDatabaseResource res = new PoolableDatabaseResource(
                getDecoratedRouter().locate(key));
        return locate(res);
    }

    public PoolableDatabaseResource locate(Key key, Function function) {
        PoolableDatabaseResource res = new PoolableDatabaseResource(
                getDecoratedRouter().locate(key, function));
        return locate(res);
    }

    @Override
    public Map<Long, SliceResourceGroup<Key, PoolableDatabaseResource>> groupLocate(
            Function function, List<Key> keys) {
        Map<Long, SliceResourceGroup<Key, PoolableDatabaseResource>> sliceResourceMap = new HashMap<>();
        for (Key key : keys) {
            PoolableDatabaseResource sr = locate(key, function);
            if (sr != null) {
                SliceResourceGroup<Key, PoolableDatabaseResource> ks = sliceResourceMap.get(sr.getSliceId());
                if (ks == null) {
                    ks = new SliceResourceGroup<>();
                }
                ks.addKey(key);
                sliceResourceMap.put(sr.getSliceId(), ks);
            }
        }
        return sliceResourceMap;
    }

    @Override
    public Map<Long, SliceResourceGroup<Key, PoolableDatabaseResource>> groupLocate(List<Key> keys) {
        return groupLocate(Function.Read, keys);
    }

    public void followSetup() {
        Map<Long, Slice<Key>> slices = getSlices();
        Set<Entry<Long, Slice<Key>>> entries = slices.entrySet();
        for (Entry<Long, Slice<Key>> entry : entries) {
            Slice<Key> slice = entry.getValue();
            List<SliceResource> availableResources = slice.getResources();
            for (SliceResource facade : availableResources) {
                PoolableDatabaseResource resource = new PoolableDatabaseResource(
                        facade);
                locate(resource);
            }

        }
    }

    public UrlMaker getUrlMaker() {
        return urlMaker;
    }

    private PoolableDatabaseResource locate(PoolableDatabaseResource res) {
        String url = res.getURL();
        if (url == null || "".equals(url)) {
            if (urlMaker == null) {
                throw new SliceRuntimeException(
                        "please config url, or host/port and set ConnectonUrlMaker instance.");
            } else {
                url = urlMaker.makeUrl(res);
            }

        }
        // String url = urlMaker.makeUrl(res);
        DataSource dataSource = pooledDataSources.get(url);
        if (dataSource == null) {
            try {
                dataSource = poolableDataSourceFactory.createDataSource(
                        res.getDriverClass(), url, res.getUsername(),
                        res.getPassword(), res.getExtraInfo());
                pooledDataSources.put(url, dataSource);
                log.debug(String.format("create pool for url: %s", url));
                log.debug(String.format("create pool for user: %s",
                        res.getUsername()));
            } catch (PoolableException e) {
                throw new SliceRuntimeException(
                        "Can't create datasource for the slice " + res, e);
            }
        }
        if (dataSource == null) {
            throw new NonExistedSliceException(
                    "Can't get datasource for the slice" + res);
        }
        res.setDataSource(dataSource);
        return res;
    }

    //
    // public Connection getConnection(Source key) throws SliceException {
    // PoolablePoolableDatabaseResource slice = get(key);
    // DataSource dataSource = getDataSource(slice);
    // if (dataSource == null) {
    // throw new SliceException("");
    // } else {
    // try {
    // return dataSource.getConnection();
    // } catch (SQLException e) {
    //
    // throw new SliceException("Can't get connection for slice "
    // + slice, e);
    // }
    // }
    // }
    //
    // public Connection getConnection(Source key, Function function)
    // throws SliceException {
    // PoolablePoolableDatabaseResource slice = get(key, function);
    // DataSource dataSource = getDataSource(slice);
    // if (dataSource == null) {
    // throw new SliceException("");
    // } else {
    // try {
    // return dataSource.getConnection();
    // } catch (SQLException e) {
    // throw new SliceException(String.format(
    // "Can't get connection by Function(%s), for slice %s",
    // function.name(), slice), e);
    // }
    // }
    // }
    public static Map<String, DataSource> allPooledDataSources() {
        return pooledDataSources;
    }

    public PoolableDataSourceFactory getPoolableDataSourceFactory() {
        return poolableDataSourceFactory;
    }

    @Override
    public String toString() {
        return "PoolableDatabaseRouter [urlMaker=" + urlMaker
                + ", poolableDataSourceFactory=" + poolableDataSourceFactory
                + ", decoratedRouter=" + decoratedRouter + "]";
    }

    @Override
    public PoolableDatabaseResource first() {
        return new PoolableDatabaseResource(getDecoratedRouter().first());
    }

    @Override
    public PoolableDatabaseResource first(Function function) {
        return new PoolableDatabaseResource(getDecoratedRouter()
                .first(function));
    }

    @Override
    public PoolableDatabaseResource last() {
        return new PoolableDatabaseResource(getDecoratedRouter().last());
    }

    @Override
    public PoolableDatabaseResource last(Function function) {
        return new PoolableDatabaseResource(getDecoratedRouter().last(function));
    }

    public Set<PoolableDatabaseResource> getPoolableDatabaseResources() {
        Set<Entry<Long, Slice<Key>>> entries = getSlices().entrySet();
        Set<PoolableDatabaseResource> resources = new HashSet<>();
        for (Entry<Long, Slice<Key>> entry : entries) {
            Slice<Key> slice = entry.getValue();
            List<SliceResource> sliceResources = slice.getTribe()
                    .getAvailableResources();
            for (SliceResource sliceResource : sliceResources) {
                resources.add(new PoolableDatabaseResource(sliceResource));
            }
            sliceResources = slice.getTribe().getFailResources();
            for (SliceResource sliceResource : sliceResources) {
                resources.add(new PoolableDatabaseResource(sliceResource));
            }
        }

        return resources;
    }
}
