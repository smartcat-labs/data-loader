package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;

public class RangeRuleLongTest {

    @Test
    public void should_set_number_of_cards_randomly_from_range() {

        Randomizer randomizerMock = new RandomizerMock();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizerMock);

        List<User> builtUsers = randomUserBuilder.randomFromRange("numberOfCards", 0L, 5L)
                .build(5);

        Assert.assertEquals(0L, builtUsers.get(0).getNumberOfCards());
        Assert.assertEquals(1, builtUsers.get(1).getNumberOfCards());
        Assert.assertEquals(2, builtUsers.get(2).getNumberOfCards());
        Assert.assertEquals(3, builtUsers.get(3).getNumberOfCards());
        Assert.assertEquals(4, builtUsers.get(4).getNumberOfCards());

    }

}
