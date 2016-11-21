package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility for calculating range operations.
 */
public class RangeUtil {

    /**
     * Ranges [a,b) and [c,d) intersect if a<=c && dd <= b.
     * @param range1
     * @param range2
     * @return true if ranges intersect, otherwise false
     */
    public static <T extends Comparable<T>> boolean rangesIntersects(List<T> range1, List<T> range2) {
        return range1.get(0).compareTo(range2.get(1)) < 1 && range2.get(0).compareTo(range1.get(1)) < 1;
    }

    /**
     * Recalculates the range when compared with exclusive range.
     * @param range
     * @param exclusiveRanges
     * @return new range
     */
    public static <T extends Comparable<T>> List<T> recalculateRanges(List<T> range, List<T> exclusiveRanges) {

        T x1 = range.get(0);
        T x2 = range.get(1);
        T y1 = exclusiveRanges.get(0);
        T y2 = exclusiveRanges.get(1);

        if (y1.compareTo(x1) < 1 && x2.compareTo(y2) < 1) { // 1.
            // ----x1----------x2----
            // -y1---------------y2--
            // -y1-------------y1----
            // ----y1------------y2--
            // ----y1----------y2----
            return new ArrayList<>();
        }
        // if (y1 <= x1 && x2 <= y2) { // 1.
        // // ----x1----------x2----
        // // -y1---------------y2--
        // // -y1-------------y1----
        // // ----y1------------y2--
        // // ----y1----------y2----
        // return new ArrayList<>();
        // }

        if (x1.compareTo(y1) < 0 && y2.compareTo(x2) < 0) { // 2.
            // ----x1----------x2----
            // --------y1--y2--------
            return Arrays.asList(x1, y1, y2, x2);
        }
        // if (x1 < y1 && y2 < x2) { // 2.
        // // ----x1----------x2----
        // // --------y1--y2--------
        // return Arrays.asList(x1, y1, y2, x2);
        // }

        if (y1.compareTo(x1) < 1) { // x2 > y2, otherwise 1.
            // ----x1----------x2----
            // -y1--------y2--------- <
            // ----y1-----y2--------- =
            return Arrays.asList(y2, x2);
        }

        if (x2.compareTo(y2) < 1) { // x1 < y1 otherwise 1.
            // ----x1----------x2----
            // --------y1--------y2--
            return Arrays.asList(x1, y1);
        }

        throw new IllegalStateException("Unexpected error: x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2);
    }

}
