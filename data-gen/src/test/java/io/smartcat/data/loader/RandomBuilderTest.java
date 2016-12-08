package io.smartcat.data.loader;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class RandomBuilderTest {

    @Test
    public void should_throw_illegal_argument_exception_when_dates_are_equal() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        try {
            randomUserBuilder.randomFromRange("dateField", LocalDateTime.of(2000, 1, 1, 0, 0),
                    LocalDateTime.of(2000, 1, 1, 0, 0));
            Assert.fail("should throw illegal argument exception because upper bound is equal to lower.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void should_throw_illegal_argument_exception_when_lower_bound_date_is_greater_than_upper_bound_date() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        try {
            randomUserBuilder.randomFromRange("dateField", LocalDateTime.of(2000, 1, 1, 0, 1),
                    LocalDateTime.of(2000, 1, 1, 0, 0));
            Assert.fail("should throw illegal argument exception because lower bound is greater than upper.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void should_throw_illegal_argument_exception_when_doubles_are_equal() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        try {
            randomUserBuilder.randomFromRange("double", 2.0, 2.0);
            Assert.fail("should throw illegal argument exception because upper bound is equal to lower.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void should_throw_illegal_argument_exception_when_lower_bound_double_is_greater_than_upper_bound_doublel() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        try {
            randomUserBuilder.randomFromRange("double", 2.01, 2.0);
            Assert.fail("should throw illegal argument exception because lower bound is greater than upper.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

}
