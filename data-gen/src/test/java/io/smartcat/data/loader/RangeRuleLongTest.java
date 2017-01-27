package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class RangeRuleLongTest {

    @Test
    public void should_set_number_of_cards_randomly_from_range() {

        Randomizer randomizerMock = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizerMock);

        randomUserBuilder.randomFromRange("numberOfCards", 0L, 5L).toBeBuilt(1000);
        BuildRunner<User> runner = new BuildRunner<>();
        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();

        Assert.assertEquals(1000, builtUsers.size());

        for (User u : builtUsers) {
            String message = "user should have number of cards in range [0,5), but is was: " + u.getNumberOfCards();
            Assert.assertTrue(message, u.getNumberOfCards() >= 0L && u.getNumberOfCards() < 5L);
        }

    }

}
