package io.smartcat.ranger.load.generator.rategenerator;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class PeriodicRateGeneratorTest {

    @Test
    public void rate_should_return_1_when_period_is_100_and_time_is_201() {
        // GIVEN
        ConcretePeriodicRateGenerator periodicRateGenerator = new ConcretePeriodicRateGenerator(100);

        // WHEN
        long rate = periodicRateGenerator.getRate(TimeUnit.SECONDS.toNanos(201));

        // THEN
        Assert.assertEquals(1, rate);
    }

    @Test
    public void rate_should_return_0_when_period_is_50_and_time_is_150() {
        // GIVEN
        ConcretePeriodicRateGenerator periodicRateGenerator = new ConcretePeriodicRateGenerator(50);

        // WHEN
        long rate = periodicRateGenerator.getRate(TimeUnit.SECONDS.toNanos(150));

        // THEN
        Assert.assertEquals(0, rate);
    }

    @Test
    public void rate_should_return_60_when_period_is_10_and_time_is_16() {
        // GIVEN
        ConcretePeriodicRateGenerator periodicRateGenerator = new ConcretePeriodicRateGenerator(10);

        // WHEN
        long rate = periodicRateGenerator.getRate(TimeUnit.SECONDS.toNanos(16));

        // THEN
        Assert.assertEquals(60, rate);
    }

    @Test
    public void rate_should_return_99_when_period_is_100_and_time_is_399() {
        // GIVEN
        ConcretePeriodicRateGenerator periodicRateGenerator = new ConcretePeriodicRateGenerator(100);

        // WHEN
        long rate = periodicRateGenerator.getRate(TimeUnit.SECONDS.toNanos(399));

        // THEN
        Assert.assertEquals(99, rate);
    }

    private class ConcretePeriodicRateGenerator extends PeriodicRateGenerator {

        public ConcretePeriodicRateGenerator(long periodInSeconds) {
            super(periodInSeconds);
        }

        @Override
        protected long rateFunction(double value) {
            return Math.round(value * 100);
        }
    }
}
