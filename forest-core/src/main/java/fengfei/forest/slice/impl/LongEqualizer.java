package fengfei.forest.slice.impl;

import fengfei.forest.slice.Equalizer;

public class LongEqualizer implements  Equalizer<Long> {

	@Override
	public long get(Long key, int sliceSize) {
		return key;//Math.abs(key % sliceSize) + 1;
	}

}
