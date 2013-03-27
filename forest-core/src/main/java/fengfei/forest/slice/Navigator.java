package fengfei.forest.slice;

import fengfei.forest.slice.SliceResource.Function;

public interface Navigator<Key> {

	/**
	 * 定位到一个给定的key和特定function 所对应的Resource
	 * 
	 * @param key
	 *            the key is Source key type of Slice equalizer
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
	 * @param key
	 * @return
	 */
	SliceResource first();

	/**
	 * get first slice of all slices by function
	 * 
	 * @param key
	 * @return
	 */
	SliceResource first(Function function);

	/**
	 * get first slice of last slices
	 * 
	 * @param key
	 * @return
	 */
	SliceResource last();

	/**
	 * get first slice of all slices by function
	 * 
	 * @param key
	 * @return
	 */
	SliceResource last(Function function);
}