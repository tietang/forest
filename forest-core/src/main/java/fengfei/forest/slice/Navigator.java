package fengfei.forest.slice;

import fengfei.forest.slice.SliceResource.Function;

import java.util.List;
import java.util.Map;

public interface Navigator<Key> {

    /**
     * 分组定位到一个给定的key和特定function 所对应的Resource
     *
     * @param keys     the key is Source key type of Slice equalizer
     * @param function
     * @return
     */
     Map<? extends SliceResource, List<Key>> groupLocate(Function function, Key... keys);

    Map<? extends SliceResource, List<Key>> groupLocate(Key... keys);

    Map<? extends SliceResource, List<Key>> groupLocate(Function function, List<Key> keys);

    Map<? extends SliceResource, List<Key>> groupLocate(List<Key> keys);

    /**
     * 分组定位到一个给定的key和特定function 所对应的Resource
     *
     * @param key      the key is Source key type of Slice equalizer
     * @param function
     * @return
     */
    SliceResource locate(Key key, Function function);

    /**
     * 定位到一个给定的key所对应的默认Resource
     *
     * @param key
     * @return
     */
    SliceResource locate(Key key);

    /**
     * get first slice of all slices
     *
     * @return
     */
    SliceResource first();

    /**
     * get first slice of all slices by function
     *
     * @return
     */
    SliceResource first(Function function);

    /**
     * get first slice of last slices
     *
     * @return
     */
    SliceResource last();

    /**
     * get first slice of all slices by function
     *
     * @return
     */
    SliceResource last(Function function);
}