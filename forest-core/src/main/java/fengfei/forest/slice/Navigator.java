package fengfei.forest.slice;

import fengfei.forest.slice.Resource.Function;

public interface Navigator<Key> {

	/**
	 * 定位到一个给定的key和特定function 所对应的Resource
	 * 
	 * @param key
	 *            the key is Source key type of Slice equalizer
	 * @param function
	 * @return
	 */
	Resource locate(Key key, Function function);

	/**
	 * 定位到一个给定的key所对应的默认Resource
	 * 
	 * @param key
	 * @return
	 */
	Resource locate(Key key);

	/**
	 * get first slice of all slices
	 * 
	 * @param key
	 * @return
	 */
	Resource first();

	/**
	 * get first slice of all slices by function
	 * 
	 * @param key
	 * @return
	 */
	Resource first(Function function);

	/**
	 * get first slice of last slices
	 * 
	 * @param key
	 * @return
	 */
	Resource last();

	/**
	 * get first slice of all slices by function
	 * 
	 * @param key
	 * @return
	 */
	Resource last(Function function);
}