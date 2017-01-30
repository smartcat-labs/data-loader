package io.smartcat.data.loader;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.Address;
import io.smartcat.data.loader.model.User;

public class UserTest {

    @Test
    public void should_build_one_user_with_correctly_set_properties() {
        LocalDateTime mayTheFirst = LocalDateTime.of(1975, 5, 1, 0, 0);
        LocalDateTime mayTheSecond = LocalDateTime.of(1975, 5, 2, 0, 0);

        RandomBuilder<Address> randomAddressBuilder = new RandomBuilder<Address>(Address.class);
        randomAddressBuilder.randomFrom("city", "Isengard").randomFrom("street", "White Wizzard Boulevard")
                .randomFromRange("houseNumber", 5L, 6L);

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        randomUserBuilder.randomFrom("username", "destroyerOfW0rldz").randomFrom("firstname", "alice")
                .randomFrom("lastname", "annison").randomFromRange("numberOfCards", 1L, 2L)
                .randomFromRange("accountBalance", 2.72, 2.73).randomSubListFrom("favoriteMovies", "Predator")
                .randomSubsetFrom("nicknames", "al").randomFromRange("birthDate", mayTheFirst, mayTheSecond)
                .randomWithBuilder("address", randomAddressBuilder).toBeBuilt(1);

        List<User> userList = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        User u = userList.get(0);

        Assert.assertEquals("destroyerOfW0rldz", u.getUsername());
        Assert.assertEquals("alice", u.getFirstname());
        Assert.assertEquals("annison", u.getLastname());
        Long expectedNumberOfCards = 1L;
        Assert.assertEquals(expectedNumberOfCards, u.getNumberOfCards());
        Assert.assertTrue(u.getAccountBalance() - 2.72 < 0.1);
        Assert.assertTrue(
                u.getFavoriteMovies().isEmpty() || (u.getFavoriteMovies().size() == 1 && u.getFavoriteMovies().get(0)
                        .equals("Predator")));
        Assert.assertTrue(
                u.getNicknames().isEmpty() || (u.getNicknames().size() == 1 && u.getNicknames().contains("al")));
        Assert.assertTrue("birthdate should be equals or after the May 1, 1975",
                u.getBirthDate().compareTo(Date.from(mayTheFirst.toInstant(ZoneOffset.UTC))) >= 0);
        Assert.assertTrue("birthdate should be before May 2, 1975",
                u.getBirthDate().compareTo(Date.from(mayTheSecond.toInstant(ZoneOffset.UTC))) < 0);
        Assert.assertEquals(u.getAddress().getCity(), "Isengard");
        Assert.assertEquals(u.getAddress().getHouseNumber(), 5L);
        Assert.assertEquals(u.getAddress().getStreet(), "White Wizzard Boulevard");

    }

}
