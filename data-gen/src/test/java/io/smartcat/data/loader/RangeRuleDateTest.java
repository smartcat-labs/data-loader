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

        randomUserBuilder.randomFromRange("birthDate", tenDaysAgo, threeDaysAgo).toBeBuilt(1000);
        BuildRunner<User> runner = new BuildRunner<>();
        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();


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

}
