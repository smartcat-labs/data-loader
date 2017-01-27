package io.smartcat.data.loader;

import java.util.List;

/**
 * Utility for calculating range operations.
 */
public class RangeUtil {

    private RangeUtil() {
    }

    /**
     * Ranges [a,b) and [c,d) intersect if a<=c && dd <= b.
     *
     * @param <T> Type parameter
     * @param range1 range represented with List
     * @param range2 range represented with List
     * @return true if ranges intersect, otherwise false
     */
    public static <T extends Comparable<T>> boolean rangesIntersects(List<T> range1, List<T> range2) {
        return range1.get(0).compareTo(range2.get(1)) < 1 && range2.get(0).compareTo(range1.get(1)) < 1;
    }

}
