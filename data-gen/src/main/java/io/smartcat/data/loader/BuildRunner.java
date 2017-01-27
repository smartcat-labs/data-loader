package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that manages builders and recalculates the rules in those builder.
 *
 * @param <T>
 */
public class BuildRunner<T> {

    private final Set<RandomBuilder<T>> builderSet = new HashSet<>();

    /**
     * Build all random builders in the list.
     *
     * @return List of built typed random builders
     */
    public List<T> build() {
        List<T> resultList = new ArrayList<>();
        for (RandomBuilder<T> builder : builderSet) {
            List<T> entityList = builder.buildAll();
            resultList.addAll(entityList);
        }
        return resultList;
    }

    /**
     * Add builder to the list.
     *
     * @param builder Typed random builder
     */
    public void addBuilder(RandomBuilder<T> builder) {
        builderSet.add(builder);
    }

}
