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

        List<User> builtUsers = randomUserBuilder.randomFromRange("numberOfCards", 0L, 5L).build(1000);

        Assert.assertEquals(1000, builtUsers.size());

        for (User u : builtUsers) {
            String message = "user should have number of cards in range [0,5), but is was: " + u.getNumberOfCards();
            Assert.assertTrue(message, u.getNumberOfCards() >= 0L && u.getNumberOfCards() < 5L);
        }

    }

    @Test
    public void should_correctly_calculate_precedance_in_range_long_rules() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        randomUserBuilder.randomFromRange("numberOfCards", 1L, 10L).randomFrom("firstname", "alice", "bob", "charlie")
                .toBeBuilt(1000);

        RandomBuilder<User> exclusiveBuilder = new RandomBuilder<User>(User.class, randomizer);
        exclusiveBuilder.exclusiveRandomFromRange("numberOfCards", 3L, 7L).randomFrom("firstname", "delta")
                .toBeBuilt(500);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        runner.addBuilder(exclusiveBuilder);

        List<User> userList = runner.build();

        Assert.assertEquals(1500, userList.size());

        System.out.println("userList.size = " + userList.size());

        int others = 0;
        int deltas = 0;
        for (User u : userList) {
            if (u.getFirstname().equals("delta")) {
                String message = "delta must have number of cards in range [3,7), but is was: " + u.getNumberOfCards();
                Assert.assertTrue(message, u.getNumberOfCards() >= 3L && u.getNumberOfCards() < 7L);
                deltas++;
            } else {
                boolean isBetweenZeroAndThree = u.getNumberOfCards() >= 0 && u.getNumberOfCards() < 3;
                boolean isBetweenSevenAndTen = u.getNumberOfCards() >= 7 && u.getNumberOfCards() < 10;
                String message = "non-delta must have number of cards in ranges [0,3) or [7,10)), but is was: "
                        + u.getNumberOfCards();
                Assert.assertTrue(message, isBetweenZeroAndThree || isBetweenSevenAndTen);
                others++;
            }
        }

        Assert.assertEquals(500, deltas);
        Assert.assertEquals(1000, others);
    }

}
