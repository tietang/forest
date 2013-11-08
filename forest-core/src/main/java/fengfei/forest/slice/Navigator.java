package fengfei.forest.slice;

import fengfei.forest.slice.SliceResource.Function;

import java.util.List;
import java.util.Map;

public interface Navigator<Key, R extends SliceResource> {

    /**
     * 分组定位到一个给定的key和特定function 所对应的Resource
     *
     * @param keys     the key is Source key type of Slice equalizer
     * @param function
     * @return
     */

    Map<R, List<Key>> groupLocate(Function function, List<Key> keys);

    Map<R, List<Key>> groupLocate(List<Key> keys);

    /**
     * 分组定位到一个给定的key和特定function 所对应的Resource
     *
     * @param key      the key is Source key type of Slice equalizer
     * @param function
     * @return
     */
    R locate(Key key, Function function);

    /**
     * 定位到一个给定的key所对应的默认Resource
     *
     * @param key
     * @return
     */
    R locate(Key key);

    /**
     * get first slice of all slices
     *
     * @return
     */
    R first();

    /**
     * get first slice of all slices by function
     *
     * @return
     */
    R first(Function function);

    /**
     * get first slice of last slices
     *
     * @return
     */
    R last();

    /**
     * get first slice of all slices by function
     *
     * @return
     */
    R last(Function function);
}