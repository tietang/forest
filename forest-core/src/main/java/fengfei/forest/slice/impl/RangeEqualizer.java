package fengfei.forest.slice.impl;

import fengfei.forest.slice.Equalizer;

public abstract class RangeEqualizer<Source> implements Equalizer<Source> {

	@Override
	public abstract long get(Source key, int sliceSize);
}
