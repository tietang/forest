package fengfei.forest.slice.equalizer;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import fengfei.forest.slice.Equalizer;

/**
 * <pre>
 * sliceSize=6 and moduleSize=1024
 * 1=[1, 7, 13, 19, 25, 31, 37, 43, 49, 55, 61, 67, 73, 79, 85, 91, 97]
 * 2=[2, 8, 14, 20, 26, 32, 38, 44, 50, 56, 62, 68, 74, 80, 86, 92, 98]
 * 3=[3, 9, 15, 21, 27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87, 93, 99]
 * 4=[4, 10, 16, 22, 28, 34, 40, 46, 52, 58, 64, 70, 76, 82, 88, 94, 100]
 * 5=[5, 11, 17, 23, 29, 35, 41, 47, 53, 59, 65, 71, 77, 83, 89, 95]
 * 6=[6, 12, 18, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96]
 * 
 * </pre>
 * 
 * @author wtt
 * 
 */
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
		
		long module = key % moduleSize+1;
		long index = Math.abs(module % sliceSize);
		return index;// == 0 ? moduleSize : module;
	}

	public static void main(String[] args) {
		int size = 6;
		MultiMap map = new MultiValueMap();
		Random random = new Random();
		LoopRoundEqualizer e = new LoopRoundEqualizer(6);
		for (int i = 1; i <= 100; i++) {
			long key = i+1024;
			// key = random.nextLong();
			long index = e.get(key, size);
			map.put(index, key);
			// System.out.println(i + " : " + index);
			// System.out.println(i + " : " + e.get(random.nextLong(), 3));
		}
		Set<Entry> set = map.entrySet();
		for (Entry entry : set) {
			List list = (List) entry.getValue();
			System.out.println(entry);
			// System.out.println(entry.getKey() + "=" + list.size());
		}

	}
}
