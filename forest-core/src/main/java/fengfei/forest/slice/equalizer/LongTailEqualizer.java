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
 * 
 * sliceSize=9 and tailSize=1 
 * 0=11  0=[500, 180, 750, 650, 470, 820, 330, 660, 180, 520, 290]
 * 1=6  1=[441, 851, 451, 751, 831, 311]
 * 2=12  2=[112, 692, 12, 132, 342, 112, 212, 352, 702, 202, 412, 72]
 * 3=12  3=[513, 763, 383, 913, 733, 553, 313, 43, 253, 43, 903, 823]
 * 4=7  4=[194, 924, 394, 154, 404, 894, 314]
 * 5=10  5=[345, 895, 875, 385, 835, 755, 255, 105, 685, 275]
 * 6=11  6=[976, 876, 536, 306, 806, 786, 616, 396, 266, 676, 26]
 * 7=11  7=[137, 857, 377, 667, 747, 827, 247, 617, 327, 287, 107]
 * 8=11  8=[648, 598, 658, 398, 198, 588, 378, 858, 448, 678, 948]
 * 9=9  9=[749, 799, 249, 759, 879, 139, 259, 309, 399]
 * 
 * 
 * </pre>
 * 
 * @author wtt
 * 
 */
public class LongTailEqualizer implements Equalizer<Long> {
	private int tailSize = 2;

	public LongTailEqualizer() {
	}

	public LongTailEqualizer(int tailSize) {
		super();
		this.tailSize = tailSize;
		if (tailSize < 1 && tailSize > 4) {
			throw new IllegalArgumentException("1<=tailSize<=4");
		}
	}

	@Override
	public long get(Long key, int sliceSize) {
		String str = String.valueOf(key);
		String skey = str;
		if (str.length() > tailSize) {
			skey = str.substring(str.length() - tailSize);
		}
		return Long.parseLong(skey);
	}

	public static void main(String[] args) {
		int size = 10;
		MultiMap map = new MultiValueMap();
		Random random = new Random();
		LongTailEqualizer e = new LongTailEqualizer(2);
		for (int i = 1; i <= 1000; i++) {
			long key = i + 98;
//			key = Math.abs(random.nextLong() % 1000);
			long index = e.get(key, size);
			map.put(index, key);
			System.out.println(i + " : " + key + " : " + index);
			// System.out.println(i + " : " + e.get(random.nextLong(), 3));
		}
		Set<Entry> set = map.entrySet();
		for (Entry entry : set) {
			List list = (List) entry.getValue();

			System.out.println(entry.getKey() + "=" + list.size() + "  "
					+ entry);
			// System.out.println(entry.getKey() + "=" + list.size() );
		}

	}
}
