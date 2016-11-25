package io.smartcat.data.loader;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class RangeRuleDateTest {

    @Test
    public void should_set_birth_date_randomly_from_range() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        List<User> builtUsers = randomUserBuilder.randomFromRange("birthDate", tenDaysAgo, threeDaysAgo)
                .build(1000);

        Assert.assertEquals(1000, builtUsers.size());

        for (User u : builtUsers) {
            String message = "user must be born between 10 days ago and 3 days ago, but was: " + u.getBirthDate();
            boolean isExactlyTenDaysAgo = u.getBirthDate().toInstant().equals(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isAfterTenDaysAgo = u.getBirthDate().toInstant().isAfter(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isBeforeThreeDaysAgo = u.getBirthDate().toInstant()
                    .isBefore(threeDaysAgo.toInstant(ZoneOffset.UTC));
            Assert.assertTrue(message, (isExactlyTenDaysAgo || isAfterTenDaysAgo) && isBeforeThreeDaysAgo);
        }
    }

    @Test
    public void should_correctly_calculate_precedance() {
        Randomizer randomizer = new RandomizerImpl();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);
        randomUserBuilder
            .randomFromRange("birthDate", tenDaysAgo, threeDaysAgo)
            .randomFrom("username", "alice")
            .toBeBuilt(1000);

        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime fiveDaysAgo = now.minusDays(5);

        RandomBuilder<User> exclusiveBuilder = new RandomBuilder<User>(User.class, randomizer);
        exclusiveBuilder
            .exclusiveRandomFromRange("birthDate", sevenDaysAgo, fiveDaysAgo)
            .randomFrom("username", "bob")
            .toBeBuilt(500);

        BuildRunner<User> buildRunner = new BuildRunner<>();
        buildRunner.addBuilder(randomUserBuilder);
        buildRunner.addBuilder(exclusiveBuilder);

        List<User> builtUsers = buildRunner.build();
        Assert.assertEquals(1500, builtUsers.size());

        int alices = 0;
        int bobs = 0;
        for (User u : builtUsers) {

            boolean isExactlyTenDaysAgo = u.getBirthDate().toInstant().equals(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isAfterTenDaysAgo = u.getBirthDate().toInstant().isAfter(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isBeforeThreeDaysAgo = u.getBirthDate().toInstant()
                    .isBefore(threeDaysAgo.toInstant(ZoneOffset.UTC));

            boolean isBeforeSevenDaysAgo = u.getBirthDate().toInstant()
                    .isBefore(sevenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isAfterSevenDaysAgo = u.getBirthDate().toInstant().isAfter(sevenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isExactlySevenDaysAgo = u.getBirthDate().toInstant().equals(sevenDaysAgo.toInstant(ZoneOffset.UTC));

            boolean isExactlyFiveDaysAgo = u.getBirthDate().toInstant().equals(fiveDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isBeforeFiveDaysAgo = u.getBirthDate().toInstant().isBefore(fiveDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isAfterFiveDaysAgo = u.getBirthDate().toInstant().isAfter(fiveDaysAgo.toInstant(ZoneOffset.UTC));

            if (u.getUsername().equals("alice")) {
                alices++;
                String message = "alice be born in allowed range, but was: " + u.getBirthDate();
                Assert.assertTrue(message, ((isExactlyTenDaysAgo || isAfterTenDaysAgo) && isBeforeSevenDaysAgo)
                        || ((isExactlyFiveDaysAgo || isAfterFiveDaysAgo) && isBeforeThreeDaysAgo));
            } else {
                bobs++;
                String message = "bob must be born in allowed range, but was: " + u.getBirthDate();
                Assert.assertEquals("bob", u.getUsername());
                Assert.assertTrue(message, (isExactlySevenDaysAgo || isAfterSevenDaysAgo) && isBeforeFiveDaysAgo);
            }
        }
        Assert.assertEquals(500, bobs);
        Assert.assertEquals(1000, alices);

    }

}
