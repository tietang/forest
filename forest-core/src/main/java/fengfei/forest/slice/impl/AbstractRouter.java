package fengfei.forest.slice.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;

import fengfei.forest.slice.*;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.equalizer.LongEqualizer;
import fengfei.forest.slice.exception.NonExistedResourceException;
import fengfei.forest.slice.exception.NonExistedSliceException;
import fengfei.forest.slice.exception.UnSupportedException;
import fengfei.forest.slice.plotter.LoopPlotter;

public abstract class AbstractRouter<Key> implements Router<Key, SliceResource> {

    protected Equalizer<Key> equalizer;
    protected OverflowType overflowType = OverflowType.Last;
    // protected SelectType selectType;
    protected Map<String, String> defaultExtraInfo = new HashMap<>();
    protected AtomicLong sliceIdCreator = new AtomicLong();
    protected Map<String, Resource> resources = new ConcurrentHashMap<>();
    protected ListMultimap<String, Long> resAndSlices = ArrayListMultimap.create();
    protected Plotter plotter = new LoopPlotter();

    @SuppressWarnings("unchecked")
    public AbstractRouter() {
        equalizer = (Equalizer<Key>) new LongEqualizer();
    }

    public AbstractRouter(Equalizer<Key> equalizer) {
        this.equalizer = equalizer;
    }

    public AbstractRouter(Equalizer<Key> equalizer, Plotter plotter) {
        this(equalizer);

        this.plotter = plotter;
    }

    public AbstractRouter(
            Equalizer<Key> equalizer,
            Plotter plotter,
            Map<String, String> defaultExtraInfo) {
        this(equalizer, plotter);
        this.defaultExtraInfo = defaultExtraInfo;

    }

    public void setPlotter(Plotter plotter) {
        this.plotter = plotter;
        Map<Long, Slice<Key>> slices = getSlices();
        Set<Entry<Long, Slice<Key>>> entries = slices.entrySet();
        for (Entry<Long, Slice<Key>> entry : entries) {
            entry.getValue().setPlotter(plotter);
        }
    }

    protected SliceResource dealOverflow(Key key, Function function, long id, boolean isDealOver) {
        if (!isSupported(overflowType)) {
            throw new UnSupportedException("unSupported overflowType :" + overflowType);
        }
        if (!isDealOver) {
            throw new NonExistedSliceException("id=" + id + " non-existed slice.");
        }
        switch (overflowType) {
            case First:
                return function == null ? first() : first(function);
            case Last:
                return function == null ? last() : last(function);
            case New:
                return null;
            case Exception:
                throw new NonExistedSliceException("id=" + id + " non-existed slice.");
            default:
                throw new NonExistedSliceException("id=" + id + " non-existed slice.");
        }
    }

    /**
     * get a special function slice of logicslice
     *
     * @param key
     * @param function
     * @param id
     * @param isDealOver
     * @return
     */
    protected SliceResource getResource(
            Slice<Key> slice,
            Key key,
            Function function,
            long id,
            boolean isDealOver) {
        if (slice == null) {
            return dealOverflow(key, function, id, isDealOver);
        }
        SliceResource resource = slice.get(id, function);
        if (resource == null) {
            Router<Key, SliceResource> router = slice.getChildRouter();
            return router.locate(key);
        }
        return resource;
    }

    protected SliceResource getResource(Slice<Key> slice, Key key, long id, boolean isDealOver) {
        if (slice == null || slice instanceof NullSlice) {
            return dealOverflow(key, null, id, isDealOver);
        }
        SliceResource resource = slice.getAny(id);
        if (resource == null) {
            Router<Key, SliceResource> router = slice.getChildRouter();
            return router.locate(key);
        }
        return resource;
    }

    protected AtomicLong ids = new AtomicLong();

    public void setEqualizer(Equalizer<Key> equalizer) {
        this.equalizer = equalizer;
    }

    public void setOverflowType(OverflowType overflowType) {
        if (!isSupported(overflowType)) {
            throw new UnSupportedException("unSupported overflowType :" + overflowType);
        }
        this.overflowType = overflowType;
    }

    public OverflowType getOverflowType() {
        return overflowType;
    }

    public Map<String, String> getDefaultExtraInfo() {
        return defaultExtraInfo;
    }

    public void setDefaultExtraInfo(Map<String, String> defaultExtraInfo) {
        this.defaultExtraInfo = defaultExtraInfo;
    }

    @Override
    public void register(Slice<Key> slice) {
        addslice(slice);

    }

    protected Slice<Key> updateNullSlice(Slice<Key> slice, String alias) {
        if (slice != null && slice instanceof NullSlice) {
            Long sliceId = slice.getSliceId();
            getSlices().remove(sliceId);
            slice = null;
            slice = createSlice(sliceId, alias);
        }
        return slice;
    }

    @Override
    public void register(Long sliceId, String alias, SliceResource resource) {
        Slice<Key> slice = getSlices().get(sliceId);
        if (slice == null) {
            slice = createSlice(sliceId, alias);
        }
        slice.setSliceId(sliceId);
        update(slice, alias, resource);
    }

    @Override
    public void register(Long sliceId, SliceResource resource) {
        register(sliceId, null, resource);
    }

    @Override
    public void register(SliceResource resource, Range... ranges) {
        register(resource, null, ranges);
    }

    protected Slice<Key> createSlice(Long sliceId, String alias) {
        Slice<Key> slice = new ReadWriteSlice<>(sliceId);
        if (alias == null || "".equals(alias)) {
            slice.setAlias(String.valueOf(sliceIdCreator.getAndIncrement()));
        } else {
            slice.setAlias(alias);
        }
        return slice;
    }

    private void update(Slice<Key> slice, String alias, SliceResource resource) {
        slice = updateNullSlice(slice, alias);
        Map<String, String> extraInfo = new HashMap<>(getDefaultExtraInfo());
        extraInfo.putAll(slice.getParams());
        extraInfo.putAll(resource.getExtraInfo());
        resource.addParams(extraInfo);
        resource.setAlias(slice.getAlias());
        slice.add(resource);
        if (plotter != null)
            slice.setPlotter(plotter);
        addslice(slice);
    }

    public abstract void addslice(Slice<Key> slice);

    public void update(Long sliceId, SliceResource resource) {
        Slice<Key> slice = getSlices().get(sliceId);
        if (slice == null) {
            throw new NonExistedSliceException("Non Existed slice for slice id:" + sliceId);
        }
        update(slice, slice.getAlias(), resource);
    }

    @Override
    public void remove(SliceResource resource) {
        if (resource == null || resource.getSliceId() == null) {
            throw new IllegalArgumentException("arg resource is imperfect.");
        } else if (getSlices().containsKey(resource.getSliceId())) {
            Slice<Key> slice = getSlices().get(resource.getSliceId());
            slice.remove(resource);
            resAndSlices.removeAll(resource.getName());
        } else {
            throw new NonExistedSliceException("Can't remove,non existed slice for slice id:"
                    + resource.getSliceId());
        }
    }

    @Override
    public void remove(Long sliceId) {
        if (sliceId == null) {
            throw new IllegalArgumentException("arg resource is imperfect.");
        } else if (getSlices().containsKey(sliceId)) {

            Slice<Key> slice = getSlices().remove(sliceId);
            List<SliceResource> resources = slice.getResources();
            for (SliceResource resource : resources) {
                resAndSlices.remove(resource.getName(), sliceId);
            }

        } else {
            throw new NonExistedSliceException("Can't remove, non existed slice for slice id:"
                    + sliceId);
        }
    }

    public abstract boolean isSupported(OverflowType overflowType);

    @Override
    public void register(Resource resource) {
        resources.put(resource.getName(), resource);
    }

    @Override
    public void map(Long sliceId, String alias, String resourceName, Function function) {
        Slice<Key> slice = getSlices().get(sliceId);
        if (slice == null) {
            slice = createSlice(sliceId, alias);
        }
        if (alias == null) {
            alias = slice.getAlias();
        }
        Resource resource = findResource(resourceName);

        if (resource == null) {
            throw new NonExistedResourceException("Non-exists resource, can't map resource: "
                    + resourceName);
        }
        update(slice, slice.getAlias(), new SliceResource(sliceId, function, resource));
    }

    public void map(Long sliceId, String resourceName, Function function) {
        map(sliceId, String.valueOf(sliceId), resourceName, function);
    }

    public void map(String resourceName, Function function, Range... ranges) {
        map(resourceName, null, function, ranges);
    }

    @Override
    public void registerChild(Long sliceId, Router<Key, SliceResource> childRouter) {
        Slice<Key> slice = get(sliceId);
        if (slice == null) {
            throw new NonExistedSliceException("Can't register child router, slice(" + sliceId
                    + ") is non-exists.");
        }
        slice.setChildRouter(childRouter);

    }

    @Override
    public Map<String, Resource> getResourceMap() {
        return resources;
    }

    @Override
    public Slice<Key> get(Long sliceId) {
        return getSlices().get(sliceId);
    }

//    public void startAutoFailover() {
//
//    }


    @Override
    public Map<Long, SliceResourceGroup<Key, SliceResource>> groupLocate(Function function, List<Key> keys) {
        Map<Long, SliceResourceGroup<Key, SliceResource>> sliceResourceMap = new HashMap<>();
        for (Key key : keys) {
            SliceResource sr = locate(key, function);
            if (sr != null) {
                SliceResourceGroup<Key, SliceResource> ks = sliceResourceMap.get(sr.getSliceId());
                if (ks == null) {
                    ks = new SliceResourceGroup();
                    ks.setSliceResource(sr);
                }
                ks.addKey(key);
                sliceResourceMap.put(sr.getSliceId(), ks);
            }
        }
        return sliceResourceMap;
    }

    @Override
    public Map<Long, SliceResourceGroup<Key, SliceResource>> groupLocate(List<Key> keys) {
        return groupLocate(Function.Read, keys);
    }

    protected Detector detector;

    @Override
    public Detector getDetector() {
        return detector;
    }

    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    protected Resource findResource(String name) {
        Resource resource = globalResources.get(name);
        if (resource == null) {
            resource = resources.get(name);
        }
        return resource;
    }

    public static Map<String, Resource> globalResources = new ConcurrentHashMap<>();

    public Map<String, Resource> getGlobalResources() {
        return globalResources;
    }

    public void registerGlobal(Resource resource) {
        globalResources.put(resource.getName(), resource);
    }
}
