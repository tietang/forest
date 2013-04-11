package fengfei.forest.slice.equalizer;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import fengfei.forest.slice.Equalizer;
import fengfei.forest.slice.utils.HashAlgorithms;

public class HashEqualizer<Key> implements Equalizer<Key> {

	@Override
	public long get(Key key, int sliceSize) {
		String data = key.toString();
		return Math.abs(HashAlgorithms.FNVHash1(data) % sliceSize) + 1;
	}

	public static void main(String[] args) {
		int size = 60;
		MultiMap map = new MultiValueMap();
		Random random = new Random();
		HashEqualizer<Long> e = new HashEqualizer<>();
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
