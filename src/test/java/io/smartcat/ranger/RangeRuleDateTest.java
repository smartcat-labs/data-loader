package io.smartcat.ranger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.model.User;

public class RangeRuleDateTest {

    @Test
    public void should_set_birth_date_randomly_from_range() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("birthDate", tenDaysAgo, threeDaysAgo)
                .toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        for (User u : result) {
            String message = "user must be born between 10 days ago and 3 days ago, but was: " + u.getBirthDate();
            boolean isExactlyTenDaysAgo = u.getBirthDate().toInstant().equals(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isAfterTenDaysAgo = u.getBirthDate().toInstant().isAfter(tenDaysAgo.toInstant(ZoneOffset.UTC));
            boolean isBeforeThreeDaysAgo = u.getBirthDate().toInstant()
                    .isBefore(threeDaysAgo.toInstant(ZoneOffset.UTC));
            Assert.assertTrue(message, (isExactlyTenDaysAgo || isAfterTenDaysAgo) && isBeforeThreeDaysAgo);
        }
    }
}
