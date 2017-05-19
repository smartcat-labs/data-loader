package io.smartcat.ranger.data.generator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;

public class SubListRuleTest {

    @Test
    public void should_set_list_property() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "destroyerOfW0rldz").withSubList("favoriteMovies", "Predator", "LotR")
                .toBeGenerated(1000).build();

        List<User> result = userGenerator.generateAll();

        Assert.assertEquals(1000, result.size());

        boolean atLeastOneEmptyList = false;
        boolean atLeastOneWithListOfSizeOne = false;
        boolean atLeastOneWithListOfSizeTwo = false;

        for (User u : result) {
            Assert.assertEquals("destroyerOfW0rldz", u.getUsername());
            if (u.getFavoriteMovies().isEmpty()) {
                atLeastOneEmptyList = true;
            } else if (u.getFavoriteMovies().size() == 1) {
                atLeastOneWithListOfSizeOne = true;

                String movie = u.getFavoriteMovies().get(0);
                boolean movieIsPredator = movie.equals("Predator");
                boolean movieIsLotR = movie.equals("LotR");
                Assert.assertTrue("movie must be either Predator or LotR, but was" + movie,
                        movieIsPredator || movieIsLotR);
            } else {
                atLeastOneWithListOfSizeTwo = true;
                Assert.assertEquals(u.getFavoriteMovies().size(), 2);
                Assert.assertTrue("both movies must be present in a list",
                        u.getFavoriteMovies().contains("Predator") && u.getFavoriteMovies().contains("LotR"));
            }
        }

        Assert.assertTrue("should be at least one with empty list.", atLeastOneEmptyList);
        Assert.assertTrue("should be at least one with list of size one.", atLeastOneWithListOfSizeOne);
        Assert.assertTrue("should be at least one with list of size two.", atLeastOneWithListOfSizeTwo);
    }
}
