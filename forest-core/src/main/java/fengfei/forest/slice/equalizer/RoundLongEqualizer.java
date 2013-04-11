package fengfei.forest.slice.equalizer;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

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
		long index = Math.abs(key % maxSize % sliceSize);
		return index == 0 ? sliceSize : index;
	}

	public static void main(String[] args) {
		int size = 6;
		MultiMap map = new MultiValueMap();
		Random random = new Random();
		RoundLongEqualizer e = new RoundLongEqualizer(10);
		for (int i = 1; i <= 1000000; i++) {
			long key = i;
			 key = random.nextLong();
			long index = e.get(key, size);
			map.put(index, key);
			// System.out.println(i + " : " + index);
			// System.out.println(i + " : " + e.get(random.nextLong(), 3));
		}
		Set<Entry> set = map.entrySet();
		for (Entry entry : set) {
			List list = (List) entry.getValue();
			System.out.println(entry.getKey() + "=" + list.size());
		}

	}
}
