package io.smartcat.ranger.rules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for creating a list of random values that is a sublist of passed allowed values. This class preserves the order
 * of the elements, i.e. for a list (a,b,c,d,e), sublists are: (a,b,c), (a,c,d), (b,d), but (b,a) is not sublist.
 *
 * If {@code <T>} is immutable, this class is immutable as well.
 *
 * @param <T> Type of value which will be generated.
 */
public class SubListRule<T> implements Rule<List<T>> {

    private final List<T> values;
    private final Distribution distribution;

    /**
     * Constructs rule with possible list of values from which sub list will be generated.
     *
     * @param values Possible values.
     */
    public SubListRule(List<T> values) {
        this(values, DEFAULT_DISTRIBUTION);
    }

    /**
     * Constructs rule with possible list of values from which sub list will be generated.
     *
     * @param values Possible values.
     * @param distribution Distribution to be used when generating values.
     */
    public SubListRule(List<T> values, Distribution distribution) {
        this.values = new ArrayList<>(values);
        this.distribution = distribution;
    }

    @Override
    public List<T> next() {
        return getRandomSubList(values);
    }

    private List<T> getRandomSubList(List<T> values) {
        List<T> result = new LinkedList<>();
        for (T t : values) {
            boolean shouldAdd = distribution.nextBoolean();
            if (shouldAdd) {
                result.add(t);
            }
        }
        return result;
    }
}
