package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.AggregatedObjectGenerator;
import io.smartcat.ranger.data.generator.ObjectGenerator;
import io.smartcat.ranger.data.generator.model.User;

public class RangeRuleIntTest {

    @Test
    public void should_set_number_of_shorts_randomly_from_range() {
        int lower = 0;
        int upper = 5;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfInts", lower, upper).toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        for (User u : result) {
            String message = "user should have number of ints in range [0,5), but is was: " + u.getNumberOfInts();
            Assert.assertTrue(message, u.getNumberOfInts() >= 0 && u.getNumberOfInts() < 5);
        }
    }

    @Test
    public void should_set_number_of_shorts_randomly_from_multi_range() {
        int lower1 = 0;
        int upper1 = 5;
        int lower2 = 10;
        int upper2 = 15;
        int lower3 = 20;
        int upper3 = 25;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfInts", lower1, upper1, lower2, upper2, lower3, upper3).toBeGenerated(1000)
                .build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        boolean atLeastOneInThirdRange = false;
        for (User u : result) {
            String message = "user should have number of ints in range [0,5) or [10,15), but it was: "
                    + u.getNumberOfInts();
            boolean inFirstRange = u.getNumberOfInts() >= lower1 && u.getNumberOfInts() < upper1;
            if (inFirstRange) {
                atLeastOneInFirstRange = true;
            }

            boolean inSecondRange = u.getNumberOfInts() >= lower2 && u.getNumberOfInts() < upper2;

            if (inSecondRange) {
                atLeastOneInSecondRange = true;
            }

            boolean inThirdRange = u.getNumberOfInts() >= lower3 && u.getNumberOfInts() < upper3;
            if (inSecondRange) {
                atLeastOneInThirdRange = true;
            }

            Assert.assertTrue(message, inFirstRange || inSecondRange || inThirdRange);
        }

        Assert.assertTrue(atLeastOneInFirstRange && atLeastOneInSecondRange && atLeastOneInThirdRange);
    }
}
