package fengfei.forest.slice;

/**
 * Strategy
 * 
 * @author Wang Tietang
 * 
 * @param <Key>
 *            Key type
 */
public interface Equalizer<Key> {

	long get(Key key, int sliceSize);

}
