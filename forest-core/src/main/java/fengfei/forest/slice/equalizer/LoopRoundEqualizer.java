package fengfei.forest.slice.equalizer;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import fengfei.forest.slice.Equalizer;

public class LoopRoundEqualizer implements Equalizer<Long> {

	private long moduleSize = 1024;

	public LoopRoundEqualizer() {
	}

	public LoopRoundEqualizer(long moduleSize) {
		super();
		this.moduleSize = moduleSize;
	}

	public long getModuleSize() {
		return moduleSize;
	}

	@Override
	public long get(Long key, int sliceSize) {
		long module = key % moduleSize;
		long index = Math.abs(module % sliceSize);
		return index == 0 ? sliceSize : index;
	}

	public static void main(String[] args) {
		int size = 60;
		MultiMap map = new MultiValueMap();
		Random random = new Random();
		LoopRoundEqualizer e = new LoopRoundEqualizer(1024);
		for (int i = 1; i <= 1000000; i++) {
			long key = i;
			key = random.nextLong();
			long index = e.get(key, size);
			map.put(index, key);
//			System.out.println(i + " : " + index);
			// System.out.println(i + " : " + e.get(random.nextLong(), 3));
		}
		Set<Entry> set = map.entrySet();
		for (Entry entry : set) {
			List list = (List) entry.getValue();
			System.out.println(entry.getKey() + "=" + list.size());
		}

	}
}
