package io.smartcat.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SubSetRule<T> implements Rule<Set<T>> {

    private boolean exclusive;
    private final Set<T> values = Sets.newHashSet();

    private SubSetRule() {
    }

    public static <T> SubSetRule<T> withValues(Collection<T> allowedValues) {
        SubSetRule<T> subSetRule = new SubSetRule<>();
        subSetRule.values.addAll(allowedValues);
        return subSetRule;
    }

    public static <T> SubSetRule<T> withValuesX(Collection<T> allowedValues) {
        SubSetRule<T> subSetRule = new SubSetRule<>();
        subSetRule.values.addAll(allowedValues);
        subSetRule.exclusive = true;
        return subSetRule;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    @Override
    public Rule<Set<T>> recalculatePrecedance(Rule<Set<T>> exclusiveRule) {
        return null;
    }

    @Override
    public Set<T> getRandomAllowedValue() {
        return getRandomSubset(values);
    }

    private Set<T> getRandomSubset(Set<T> values) {
        int randomSize = ThreadLocalRandom.current().nextInt(0, values.size());

        List<T> list = Lists.newArrayList(values);
        Collections.shuffle(list);
        Set<T> randomSubset = Sets.newHashSet(list.subList(0, randomSize));

        return randomSubset;
    }

}
