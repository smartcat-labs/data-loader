package io.smartcat.data.loader;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class RangeRuleDateCornerCasesTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        randomUserBuilder
            .randomFrom("username", "subzero")
            .randomFromRange("birthDate", tenDaysAgo, threeDaysAgo)
            .toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : builtUsers) {

            long timeDiffBeginingOfTheRange = u.getBirthDate().getTime()
                    - tenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange == 0) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }

            long timeDiffEndOfRange = u.getBirthDate().getTime()
                    - threeDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfRange == -1) {
                oneExactlyAtTheEndOfTheRange = true;
            }

        }

        Assert.assertTrue("One user must be born exactly 10 days ago", oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must be born a millisecond before 3 days ago", oneExactlyAtTheEndOfTheRange);
    }

    @Test
    public void should_set_low_and_high_end_values_for_multirange() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        randomUserBuilder.randomFrom("username", "subzero").randomFromRange("birthDate", tenDaysAgo, threeDaysAgo)
                .toBeBuilt(5);

        RandomBuilder<User> exclusiveRangeUserBuilder = new RandomBuilder<User>(User.class);

        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime fiveDaysAgo = now.minusDays(5);

        exclusiveRangeUserBuilder.randomFrom("username", "scorpion")
                .exclusiveRandomFromRange("birthDate", sevenDaysAgo, fiveDaysAgo).toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        runner.addBuilder(exclusiveRangeUserBuilder);
        List<User> builtUsers = runner.build();

        boolean subzeroExactly10DaysAgo = false;
        boolean subzero7DaysAgoMinusMillisecond = false;
        boolean subzeroExactly5DaysAgo = false;
        boolean subzero3DaysAgoMinusMillisecond = false;

        boolean scorpionExactly7DaysAgo = false;
        boolean scorpion5DaysAgoMinusMillisecond = false;

        for (User u : builtUsers) {

            long timeDiffBeginingOfTheRange1 = u.getBirthDate().getTime()
                    - tenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange1 == 0) {
                Assert.assertEquals("subzero", u.getUsername());
                subzeroExactly10DaysAgo = true;
            }

            long timeDiffEndOfRange1 = u.getBirthDate().getTime()
                    - sevenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfRange1 == -1) {
                Assert.assertEquals("subzero", u.getUsername());
                subzero7DaysAgoMinusMillisecond = true;
            }

            long timeDiffBeginingOfTheRange2 = u.getBirthDate().getTime()
                    - sevenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange2 == 0) {
                Assert.assertEquals("scorpion", u.getUsername());
                scorpionExactly7DaysAgo = true;
            }

            long timeDiffEndOfTheRange2 = u.getBirthDate().getTime()
                    - fiveDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfTheRange2 == -1) {
                Assert.assertEquals("scorpion", u.getUsername());
                scorpion5DaysAgoMinusMillisecond = true;
            }

            long timeDiffBeginingOfTheRange3 = u.getBirthDate().getTime()
                    - fiveDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange3 == 0) {
                Assert.assertEquals("subzero", u.getUsername());
                subzeroExactly5DaysAgo = true;
            }

            long timeDiffEndOfTheRange3 = u.getBirthDate().getTime()
                    - threeDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfTheRange3 == -1) {
                Assert.assertEquals("subzero", u.getUsername());
                subzero3DaysAgoMinusMillisecond = true;
            }
        }

        Assert.assertTrue("One subzero user must be born exactly 10 days ago", subzeroExactly10DaysAgo);
        Assert.assertTrue("One subzero user must be born a millisecond before 7 days ago",
                subzero7DaysAgoMinusMillisecond);

        Assert.assertTrue("One scorpion user must be born exactly 7 days ago", scorpionExactly7DaysAgo);
        Assert.assertTrue("One scorpion user must be born a millisecond before 5 days ago",
                scorpion5DaysAgoMinusMillisecond);

        Assert.assertTrue("One subzero user must be born exactly 5 days ago", subzeroExactly5DaysAgo);
        Assert.assertTrue("One subzero user must be born a millisecond before 3 days ago",
                subzero3DaysAgoMinusMillisecond);
    }

}
