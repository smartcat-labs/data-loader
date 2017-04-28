package io.smartcat.ranger.data.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that manages builders and recalculates the rules in those builder.
 *
 * @param <T>
 */
public class BuildRunner<T> {

    private final Set<RandomBuilder<T>> builderSet = new HashSet<>();

    /**
     * Add builder to the list.
     *
     * @param builder Typed random builder
     * @return {@code BuildRunner} instance
     */
    public BuildRunner<T> withBuilder(RandomBuilder<T> builder) {
        this.builderSet.add(builder);
        return this;
    }

    /**
     * Build all random builders in the list.
     *
     * @return List of built typed random builders
     */
    public List<T> build() {
        return builderSet.stream().map(builder -> builder.buildAll()).flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
