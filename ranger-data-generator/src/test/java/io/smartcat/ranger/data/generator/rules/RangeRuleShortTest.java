package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.AggregatedObjectGenerator;
import io.smartcat.ranger.data.generator.ObjectGenerator;
import io.smartcat.ranger.data.generator.model.User;

public class RangeRuleShortTest {

    @Test
    public void should_set_number_of_shorts_randomly_from_range() {
        short lower = 0;
        short upper = 5;

        ObjectGenerator<User> randomUserBuilder = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfShorts", lower, upper).toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(randomUserBuilder).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        for (User u : result) {
            String message = "user should have number of shorts in range [0,5), but is was: " + u.getNumberOfShorts();
            Assert.assertTrue(message, u.getNumberOfShorts() >= 0 && u.getNumberOfShorts() < 5);
        }
    }

    @Test
    public void should_set_number_of_shorts_randomly_from_multi_range() {
        short lower1 = 0;
        short upper1 = 5;
        short lower2 = 10;
        short upper2 = 15;
        short lower3 = 20;
        short upper3 = 25;

        ObjectGenerator<User> randomUserBuilder = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfShorts", lower1, upper1, lower2, upper2, lower3, upper3).toBeGenerated(1000)
                .build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(randomUserBuilder).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        boolean atLeastOneInThirdRange = false;
        for (User u : result) {
            String message = "user should have number of shorts in range [0,5) or [10,15), but it was: "
                    + u.getNumberOfShorts();
            boolean inFirstRange = u.getNumberOfShorts() >= lower1 && u.getNumberOfShorts() < upper1;
            if (inFirstRange) {
                atLeastOneInFirstRange = true;
            }

            boolean inSecondRange = u.getNumberOfShorts() >= lower2 && u.getNumberOfShorts() < upper2;

            if (inSecondRange) {
                atLeastOneInSecondRange = true;
            }

            boolean inThirdRange = u.getNumberOfShorts() >= lower3 && u.getNumberOfShorts() < upper3;
            if (inSecondRange) {
                atLeastOneInThirdRange = true;
            }

            Assert.assertTrue(message, inFirstRange || inSecondRange || inThirdRange);
        }

        Assert.assertTrue(atLeastOneInFirstRange && atLeastOneInSecondRange && atLeastOneInThirdRange);
    }
}
