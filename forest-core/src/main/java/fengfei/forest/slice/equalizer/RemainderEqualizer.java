package fengfei.forest.slice.equalizer;

import fengfei.forest.slice.Equalizer;

public class RemainderEqualizer implements Equalizer<Long> {

    @Override
    public long get(Long key, int sliceSize) {
        return Math.abs((key + sliceSize - 1) % sliceSize) + 1;
    }
}
