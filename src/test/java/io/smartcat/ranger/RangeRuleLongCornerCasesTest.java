package io.smartcat.ranger;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.model.User;

public class RangeRuleLongCornerCasesTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        Long beginingOfRange = 0L;
        Long endOfRange = 10L;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "subzero").withRanges("numberOfCards", beginingOfRange, endOfRange)
                .toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : aggregatedObjectGenerator) {
            if (u.getNumberOfCards() == 0) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }
            if (u.getNumberOfCards() == endOfRange - 1) {
                oneExactlyAtTheEndOfTheRange = true;
            }
        }

        Assert.assertTrue("One user must have numberOfCards property with value 0", oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must have numberOfCards property with value 9", oneExactlyAtTheEndOfTheRange);
    }
}
