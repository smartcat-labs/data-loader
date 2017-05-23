package io.smartcat.ranger;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.model.User;

public class RangeRuleLongTest {

    @Test
    public void should_set_number_of_cards_randomly_from_range() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfCards", 0L, 5L).toBeGenerated(1000).build();
        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        for (User u : result) {
            String message = "user should have number of cards in range [0,5), but is was: " + u.getNumberOfCards();
            Assert.assertTrue(message, u.getNumberOfCards() >= 0L && u.getNumberOfCards() < 5L);
        }
    }
}
