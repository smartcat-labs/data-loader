package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.BuildRunner;
import io.smartcat.ranger.data.generator.RandomBuilder;
import io.smartcat.ranger.data.generator.model.User;

public class RangeRuleIntTest {

    @Test
    public void should_set_number_of_shorts_randomly_from_range() {

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        int lower = 0;
        int upper = 5;
        randomUserBuilder.randomFromRange("numberOfInts", lower, upper).toBeBuilt(1000);

        List<User> builtUsers = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        Assert.assertEquals(1000, builtUsers.size());

        for (User u : builtUsers) {
            String message = "user should have number of ints in range [0,5), but is was: " + u.getNumberOfInts();
            Assert.assertTrue(message, u.getNumberOfInts() >= 0 && u.getNumberOfInts() < 5);
        }

    }

    @Test
    public void should_set_number_of_shorts_randomly_from_multi_range() {

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        int lower1 = 0;
        int upper1 = 5;
        int lower2 = 10;
        int upper2 = 15;
        int lower3 = 20;
        int upper3 = 25;
        randomUserBuilder.randomFromRange("numberOfInts", lower1, upper1, lower2, upper2, lower3, upper3)
                .toBeBuilt(1000);

        List<User> builtUsers = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        Assert.assertEquals(1000, builtUsers.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        boolean atLeastOneInThirdRange = false;
        for (User u : builtUsers) {
            String message = "user should have number of ints in range [0,5) or [10,15), but it was: " + u
                    .getNumberOfInts();
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
