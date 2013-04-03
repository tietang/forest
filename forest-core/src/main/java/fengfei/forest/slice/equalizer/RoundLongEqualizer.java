package fengfei.forest.slice.equalizer;

import fengfei.forest.slice.Equalizer;

public class RoundLongEqualizer implements Equalizer<Long> {

	private long maxKey = 2000000;

	public RoundLongEqualizer(long maxKey) {
		super();
		this.maxKey = maxKey;
	}

	public long getMaxKey() {
		return maxKey;
	}

	public void setMaxKey(long maxKey) {
		this.maxKey = maxKey;
	}

	@Override
	public long get(Long key, int sliceSize) {
		long maxSize = maxKey * sliceSize;
		long index = Math.abs(key % maxSize);
		return index == 0 ? maxSize : index;
	}

	public static void main(String[] args) {
		RoundLongEqualizer e = new RoundLongEqualizer(10);
		for (int i = 1; i < 123; i++) {
			//System.out.println(i + " : " + e.get(Long.valueOf(i), 3));
		}
	}
}
