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

    @Test
    public void should_throw_exception_when_range_elements_are_not_increasing() {

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        short lower1 = 0;
        short upper1 = 5;
        short lower2 = 10;
        short upper2 = 15;
        short lower3 = upper2;
        short upper3 = 20;
        try {
            randomUserBuilder.randomFromRange("numberOfShorts", lower1, upper1, lower2, upper2, lower3, upper3).build(1000);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid range bounds. Range definition must be stricly increasing.", e.getMessage());
        }

    }

    @Test
    public void should_throw_exception_when_ranges_are_defined_by_odd_number_of_elements() {

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        short lower1 = 0;
        short upper1 = 5;
        short lower2 = 10;
        short upper2 = 15;
        short lower3 = 15;
        try {
            randomUserBuilder.randomFromRange("numberOfShorts", lower1, upper1, lower2, upper2, lower3).build(1000);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid ranges definition. Ranges must be defined with even number of elements.", e.getMessage());
        }
    }

}
