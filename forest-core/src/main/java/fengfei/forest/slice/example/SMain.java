package fengfei.forest.slice.example;

import java.util.ArrayList;
import java.util.List;

import fengfei.forest.slice.Range;

public class SMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int size = 1000;
        List<Range[]> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            long start = i * 100 + 1;
            long end = (i + 1) * 100;
            list.add(new Range[] { new Range(start, end) });
        }
        long start = System.currentTimeMillis();
        int num = 10000000;
        for (int i = 1; i <= num; i++) {
            int index = sc(list, i);
            // System.out.println(index);

        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(num / ((end - start) / 1000));
    }

    private static int sc(List<Range[]> list, long key) {

        for (int j = 0; j < list.size(); j++) {
            Range[] ranges = list.get(j);
            for (Range range : ranges) {
                if (key >= range.start && key <= range.end) {
                    return j;
                }
            }
        }
        return -1;
    }

}
