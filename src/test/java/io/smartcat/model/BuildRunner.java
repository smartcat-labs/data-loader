package io.smartcat.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BuildRunner {

    private final Set<RandomBuilder> builderSet = Sets.newHashSet();

    public List<User> build() {
        recalculateRules();
        List<User> resultList = Lists.newArrayList();
        for (RandomBuilder builder : builderSet) {
            List<User> entityList = builder.buildAll();
            resultList.addAll(entityList);
        }
        return resultList;
    }

    public void addBuilder(RandomBuilder builder) {
        builderSet.add(builder);
    }

    private void recalculateRules() {
        Map<RandomBuilder, Map<String, Rule>> builderFieldNameExclusiveRuleMap = fetchExclusiveRules(builderSet);
        Set<RandomBuilder> builderSetWithRecalculatedRules = recalculatedBuilderSet(builderSet,
                builderFieldNameExclusiveRuleMap);

    }

    private Map<RandomBuilder, Map<String, Rule>> fetchExclusiveRules(final Set<RandomBuilder> builderSet) {
        final Map<String, Rule> fieldRuleMap = Maps.newHashMap();
        final Map<RandomBuilder, Map<String, Rule>> result = Maps.newHashMap();

        for (RandomBuilder randomBuilder : builderSet) {
            for (Entry<String, Rule<?>> entry : randomBuilder.getFieldRules().entrySet()) {
                String fieldName = entry.getKey();
                Rule rule = entry.getValue();
                if (rule.isExclusive()) {
                    fieldRuleMap.put(fieldName, rule);
                    result.put(randomBuilder, fieldRuleMap);
                }
            }
        }

        return result;
    }

    private Set<RandomBuilder> recalculatedBuilderSet(Set<RandomBuilder> builderSet,
            Map<RandomBuilder, Map<String, Rule>> builderFieldNameExclusiveRuleMap) {
        Set<RandomBuilder> result = Sets.newHashSet();

        for (Entry<RandomBuilder, Map<String, Rule>> entryBuilderFieldRule : builderFieldNameExclusiveRuleMap
                .entrySet()) {

            for (Entry<String, Rule> entryFieldExclusiveRule : entryBuilderFieldRule.getValue().entrySet()) {
                for (RandomBuilder randombuilder : builderSet) {
                    Map<String, Rule<?>> existingFieldRules = randombuilder.getFieldRules();

                    String fieldName = entryFieldExclusiveRule.getKey();
                    Rule<?> rule = existingFieldRules.get(fieldName);

                    if (rule != null && !rule.isExclusive()) {
                        Rule recalculatedRule = rule.recalculatePrecedance(entryFieldExclusiveRule.getValue());
                        existingFieldRules.put(fieldName, recalculatedRule);
                    }

                }
            }

        }

        return result;
    }

}
