package fengfei.forest.slice.equalizer;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.utils.HashAlgorithms;

public class HashEqualizer<Key> implements  Equalizer<Key> {

	@Override
	public long get(Key key, int sliceSize) {
		String data = key.toString();
		return Math.abs(HashAlgorithms.FNVHash1(data) % sliceSize) + 1;
	}

}
