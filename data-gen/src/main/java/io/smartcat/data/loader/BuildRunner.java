package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.smartcat.data.loader.rules.Rule;

/**
 * Class that manages builders and recalculates the rules in those builder.
 *
 * @param <T>
 */
public class BuildRunner<T> {

    private final Set<RandomBuilder<T>> builderSet = new HashSet<>();

    public List<T> build() {
        recalculateRules();
        List<T> resultList = new ArrayList<>();
        for (RandomBuilder<T> builder : builderSet) {
            List<T> entityList = builder.buildAll();
            resultList.addAll(entityList);
        }
        return resultList;
    }

    public void addBuilder(RandomBuilder<T> builder) {
        builderSet.add(builder);
    }

    private void recalculateRules() {
        Map<RandomBuilder<T>, Map<String, Rule<?>>> builderFieldNameExclusiveRuleMap = fetchExclusiveRules(builderSet);
        Set<RandomBuilder<T>> builderSetWithRecalculatedRules = recalculatedBuilderSet(builderSet,
                builderFieldNameExclusiveRuleMap);
    }

    private Map<RandomBuilder<T>, Map<String, Rule<?>>> fetchExclusiveRules(final Set<RandomBuilder<T>> builderSet) {
        final Map<String, Rule<?>> fieldRuleMap = new HashMap<>();
        final Map<RandomBuilder<T>, Map<String, Rule<?>>> result = new HashMap<>();

        for (RandomBuilder<T> randomBuilder : builderSet) {
            for (Entry<String, Rule<?>> entry : randomBuilder.getFieldRules().entrySet()) {
                String fieldName = entry.getKey();
                Rule<?> rule = entry.getValue();
                if (rule.isExclusive()) {
                    fieldRuleMap.put(fieldName, rule);
                    result.put(randomBuilder, fieldRuleMap);
                }
            }
        }

        return result;
    }

    private Set<RandomBuilder<T>> recalculatedBuilderSet(Set<RandomBuilder<T>> builderSet,
            Map<RandomBuilder<T>, Map<String, Rule<?>>> builderFieldNameExclusiveRuleMap) {
        Set<RandomBuilder<T>> result = new HashSet<>();

        for (Entry<RandomBuilder<T>, Map<String, Rule<?>>> entryBuilderFieldRule : builderFieldNameExclusiveRuleMap
                .entrySet()) {

            for (Entry<String, Rule<?>> entryFieldExclusiveRule : entryBuilderFieldRule.getValue().entrySet()) {
                for (RandomBuilder<T> randombuilder : builderSet) {
                    Map<String, Rule<?>> existingFieldRules = randombuilder.getFieldRules();

                    String fieldName = entryFieldExclusiveRule.getKey();
                    Rule<?> rule = existingFieldRules.get(fieldName);

                    if (rule != null && !rule.isExclusive()) {
                        Rule<?> recalculatedRule = rule.recalculatePrecedance(entryFieldExclusiveRule.getValue());
                        existingFieldRules.put(fieldName, recalculatedRule);
                    }

                }
            }

        }

        return result;
    }

}
