package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class RangeRuleLongCornerCasesTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        Long beginingOfRange = 0L;
        Long endOfRange = 10L;

        randomUserBuilder
            .randomFrom("username", "subzero")
            .randomFromRange("numberOfCards", beginingOfRange, endOfRange)
            .toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : builtUsers) {

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
