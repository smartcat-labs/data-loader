package io.smartcat.ranger.data.generator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.rules.RangeRuleDouble;

public class ObjectGeneratorTest {

    @Test
    public void should_throw_illegal_argument_exception_when_dates_are_equal() {
        try {
            new ObjectGenerator.Builder<User>(User.class).withRanges("dateField", LocalDateTime.of(2000, 1, 1, 0, 0),
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
        try {
            new ObjectGenerator.Builder<User>(User.class).withRanges("dateField", LocalDateTime.of(2000, 1, 1, 0, 1),
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
        try {
            new ObjectGenerator.Builder<User>(User.class).withRanges("double", 2.0, 2.0);
            Assert.fail("should throw illegal argument exception because upper bound is equal to lower.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void should_throw_illegal_argument_exception_when_lower_bound_double_is_greater_than_upper_bound_doublel() {
        try {
            new ObjectGenerator.Builder<User>(User.class).withRanges("double", 2.01, 2.0);
            Assert.fail("should throw illegal argument exception because lower bound is greater than upper.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void should_throw_exception_when_range_elements_are_not_increasing() {
        try {
            ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                    .withRanges("numberOfShorts", 0, 5, 10, 15, 15, 20).toBeGenerated(1000).build();
            new AggregatedObjectGenerator.Builder<User>().withObjectGenerator(userGenerator).build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid range bounds. Range definition must be stricly increasing.", e.getMessage());
        }
    }

    @Test
    public void should_throw_exception_when_ranges_are_defined_by_odd_number_of_elements() {
        try {
            ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                    .withRanges("numberOfShorts", 0, 5, 10, 15, 15).toBeGenerated(1000).build();
            new AggregatedObjectGenerator.Builder<User>().withObjectGenerator(userGenerator).build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid ranges definition. Ranges must be defined with even number of elements.",
                    e.getMessage());
        }
    }

    @Test
    public void should_create_dates_when_passed_multiple_ranges() {
        LocalDateTime date1960 = LocalDateTime.of(1960, 1, 1, 0, 0);
        LocalDateTime date1980 = LocalDateTime.of(1980, 1, 1, 0, 0);
        LocalDateTime date1990 = LocalDateTime.of(1990, 1, 1, 0, 0);
        LocalDateTime date2000 = LocalDateTime.of(2000, 1, 1, 0, 0);

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("birthDate", date1960, date1980, date1990, date2000).toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        for (User u : result) {
            boolean isAfter1960 = u.getBirthDate().toInstant().isAfter(date1960.toInstant(ZoneOffset.UTC));
            boolean isAfter1990 = u.getBirthDate().toInstant().isAfter(date1990.toInstant(ZoneOffset.UTC));
            boolean isBefore1980 = u.getBirthDate().toInstant().isBefore(date1980.toInstant(ZoneOffset.UTC));
            boolean isBefore2000 = u.getBirthDate().toInstant().isBefore(date2000.toInstant(ZoneOffset.UTC));

            boolean isExactly1960 = u.getBirthDate().toInstant().equals(date1960.toInstant(ZoneOffset.UTC));
            boolean isExactly1990 = u.getBirthDate().toInstant().equals(date1990.toInstant(ZoneOffset.UTC));
            if (isExactly1960 || (isAfter1960 && isBefore1980)) {
                atLeastOneInFirstRange = true;
            } else if (isExactly1990 || (isAfter1990 && isBefore2000)) {
                atLeastOneInSecondRange = true;
            } else {
                Assert.fail("There are dates between defined ranges.");
            }
        }

        Assert.assertTrue(atLeastOneInFirstRange);
        Assert.assertTrue(atLeastOneInSecondRange);
    }

    @Test
    public void should_create_dates() {
        Date date1960 = Date.from(LocalDateTime.of(1960, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        Date date1980 = Date.from(LocalDateTime.of(1980, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        Date date1990 = Date.from(LocalDateTime.of(1990, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        Date date2000 = Date.from(LocalDateTime.of(2000, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        List<Date> dates = Arrays.asList(date1960, date1980, date1990, date2000);

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("birthDate", dates).toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        for (User u : result) {
            boolean isAfter1960 = u.getBirthDate().after(date1960);
            boolean isAfter1990 = u.getBirthDate().after(date1990);
            boolean isBefore1980 = u.getBirthDate().before(date1980);
            boolean isBefore2000 = u.getBirthDate().before(date2000);

            boolean isExactly1960 = u.getBirthDate().equals(date1960);
            boolean isExactly1990 = u.getBirthDate().equals(date1990);
            if (isExactly1960 || (isAfter1960 && isBefore1980)) {
                atLeastOneInFirstRange = true;
            } else if (isExactly1990 || (isAfter1990 && isBefore2000)) {
                atLeastOneInSecondRange = true;
            } else {
                Assert.fail("There are dates between defined ranges.");
            }
        }

        Assert.assertTrue(atLeastOneInFirstRange);
        Assert.assertTrue(atLeastOneInSecondRange);
    }

    @Test
    public void should_accept_multiple_ranges_for_long() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("numberOfCards", 1L, 10L, 20L, 30L).toBeGenerated(1000)
                .build();
        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        for (User u : result) {
            if (u.getNumberOfCards() >= 1L && u.getNumberOfCards() < 10L) {
                atLeastOneInFirstRange = true;
            } else if (u.getNumberOfCards() >= 20L && u.getNumberOfCards() < 30L) {
                atLeastOneInSecondRange = true;
            } else {
                Assert.fail("There are values between defined ranges.");
            }
        }

        Assert.assertTrue(atLeastOneInFirstRange);
        Assert.assertTrue(atLeastOneInSecondRange);
    }

    @Test
    public void should_accept_multiple_ranges_for_double() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("accountBalance", 1.0, 10.1, 20.0, 30.1)
                .toBeGenerated(1000).build();
        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        for (User u : result) {
            boolean isInFirstRange = valueIsInRange(u.getAccountBalance(), 1.0, 10.1, RangeRuleDouble.EPSILON);
            boolean isInSecondRange = valueIsInRange(u.getAccountBalance(), 20.0, 30.1, RangeRuleDouble.EPSILON);

            if (isInFirstRange) {
                atLeastOneInFirstRange = true;
            } else if (isInSecondRange) {
                atLeastOneInSecondRange = true;
            } else {
                Assert.fail("There are values between defined ranges: " + u.getAccountBalance());
            }
        }

        Assert.assertTrue(atLeastOneInFirstRange);
        Assert.assertTrue(atLeastOneInSecondRange);
    }

    private boolean valueIsInRange(Double value, Double rangeStart, Double rangeEnd, Double epsilon) {
        boolean isAtBegining = Math.abs(rangeStart - value) <= epsilon;
        boolean isAtEnd = Math.abs(rangeEnd - value - epsilon) <= epsilon;
        boolean isInRange = Math.abs(rangeEnd - value) >= Math.abs(rangeStart);
        return isAtBegining || isAtEnd || isInRange;
    }

}
