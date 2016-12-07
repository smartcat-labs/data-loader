package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class SubSetRuleTest {

    @Test
    public void should_set_set_property() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        randomUserBuilder.randomFrom("username", "Melkor").randomSubsetFrom("nicknames", "Belegurth", "Morgoth")
                .toBeBuilt(1000);

        List<User> result = randomUserBuilder.buildAll();

        Assert.assertEquals(1000, result.size());

        boolean atLeastOneEmptySet = false;
        boolean atLeastOneWithSetOfSizeOne = false;
        boolean atLeastOneWithSetOfSizeTwo = false;

        for (User u : result) {
            Assert.assertEquals("Melkor", u.getUsername());
            if (u.getNicknames().isEmpty()) {
                atLeastOneEmptySet = true;
            } else if (u.getNicknames().size() == 1) {
                atLeastOneWithSetOfSizeOne = true;

                String nickname = u.getNicknames().stream().findFirst().get();
                boolean nicknameIsBelegurth = nickname.equals("Belegurth");
                boolean nicknameIsMorgoth = nickname.equals("Morgoth");
                Assert.assertTrue("Melkor must have nickname either Belegurth or Morgoth, but was" + nickname,
                        nicknameIsBelegurth || nicknameIsMorgoth);
            } else {
                atLeastOneWithSetOfSizeTwo = true;
                Assert.assertEquals(u.getNicknames().size(), 2);
                Assert.assertTrue("both nicknames must be present in the set",
                        u.getNicknames().contains("Belegurth") && u.getNicknames().contains("Morgoth"));
            }
        }

        Assert.assertTrue("should be at least one with empty list.", atLeastOneEmptySet);
        Assert.assertTrue("should be at least one with list of size one.", atLeastOneWithSetOfSizeOne);
        Assert.assertTrue("should be at least one with list of size two.", atLeastOneWithSetOfSizeTwo);

    }

    @Test
    public void should_calculate_precedence_with_strings() {
        RandomBuilder<User> melkorUserBuilder = new RandomBuilder<User>(User.class);
        melkorUserBuilder.randomFrom("username", "Melkor")
                .randomSubsetFrom("nicknames", "Belegurth", "Morgoth", "Stormcrow").toBeBuilt(1000);

        RandomBuilder<User> gandalfUserBuilder = new RandomBuilder<User>(User.class);
        gandalfUserBuilder.randomFrom("username", "Gandalf")
                .exclusiveRandomSubsetFrom("nicknames", "The Gray", "Stormcrow").toBeBuilt(100);

        BuildRunner<User> runner = new BuildRunner<>();
        runner.addBuilder(melkorUserBuilder);
        runner.addBuilder(gandalfUserBuilder);

        List<User> result = runner.build();

        Assert.assertEquals(1100, result.size());

        boolean atLeastOneEmptySet = false;
        boolean atLeastOneWithSetOfSizeOne = false;
        boolean atLeastOneWithSetOfSizeTwo = false;

        for (User user : result) {
            if (user.getUsername().equals("Melkor")) {
                Assert.assertTrue("Melkor should not have nickname Stormcrow",
                        !user.getNicknames().contains("Stormcrow"));
                Assert.assertTrue(
                        "Melkor should have no nicknames, or Belegurth and/or Morgoth, but had: " + user.getNicknames(),
                        user.getNicknames().isEmpty() || user.getNicknames().contains("Belegurth")
                                || user.getNicknames().contains("Morgoth"));
                if (user.getNicknames().isEmpty()) {
                    atLeastOneEmptySet = true;
                } else if (user.getNicknames().size() == 1) {
                    atLeastOneWithSetOfSizeOne = true;
                } else if (user.getNicknames().size() == 2) {
                    atLeastOneWithSetOfSizeTwo = true;
                }
            } else {
                Assert.assertEquals("Gandalf", user.getUsername());
                Assert.assertTrue("Gandalf should have no nicknames, or The Gray and/or Stormcrow",
                        user.getNicknames().isEmpty() || user.getNicknames().contains("The Gray")
                                || user.getNicknames().contains("Stormcrow"));
                if (user.getNicknames().isEmpty()) {
                    atLeastOneEmptySet = true;
                } else if (user.getNicknames().size() == 1) {
                    atLeastOneWithSetOfSizeOne = true;
                } else if (user.getNicknames().size() == 2) {
                    atLeastOneWithSetOfSizeTwo = true;
                }
            }
        }

        Assert.assertTrue("There is a very high probability that there should be at least one user with no nicknames",
                atLeastOneEmptySet);
        Assert.assertTrue("There is a very high probability that there should be at least one user with one nickname",
                atLeastOneWithSetOfSizeOne);
        Assert.assertTrue("There is a very high probability that there should be at least one user with two nicknames",
                atLeastOneWithSetOfSizeTwo);

    }

}
