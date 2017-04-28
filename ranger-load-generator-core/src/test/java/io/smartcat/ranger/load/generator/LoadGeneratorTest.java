package io.smartcat.ranger.load.generator;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.load.generator.api.DataSource;
import io.smartcat.ranger.load.generator.datasource.RandomIntDataSource;
import io.smartcat.ranger.load.generator.rategenerator.ConstantRateGenerator;
import io.smartcat.ranger.load.generator.worker.NullWorker;

public class LoadGeneratorTest {

    private static final long DEFAULT_TEST_TIMEOUT = 3000;

    @Test(expected = IllegalStateException.class)
    public void run_should_throw_exception_when_loadGenerator_is_already_terminated() {
        // GIVEN
        LoadGenerator<Integer> loadGenerator = new LoadGenerator<>(new RandomIntDataSource(),
                new ConstantRateGenerator(1000), new NullWorker<>());
        runInBackground(() -> {
            loadGenerator.run();
        });
        wait(500);

        loadGenerator.terminate();
        wait(500);

        // WHEN
        loadGenerator.run();

        // THEN
        // Exception should be thrown
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT)
    public void loadGenerator_should_be_terminated_when_terminate_signal_is_sent() {
        // GIVEN
        LoadGenerator<Integer> loadGenerator = new LoadGenerator<>(new RandomIntDataSource(),
                new ConstantRateGenerator(1000), new NullWorker<>());
        runInBackground(() -> {
            // wait a bit for loadGenerator to run
            wait(1000);
            loadGenerator.terminate();
            wait(500);
        });

        // WHEN
        loadGenerator.run();

        // THEN
        // test finishes without timeout
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT)
    public void loadGenerator_should_be_terminated_when_dataSource_has_no_next_element() {
        // GIVEN
        LoadGenerator<Integer> loadGenerator = new LoadGenerator<>(new DataSource<Integer>() {

            @Override
            public boolean hasNext(long time) {
                return false;
            }

            @Override
            public Integer getNext(long time) {
                return 123;
            }
        }, new ConstantRateGenerator(1000), new NullWorker<>());

        // WHEN
        loadGenerator.run();

        // THEN
        // test finishes without timeout
    }

    @Test
    public void worker_should_be_invoked_200_000_times_when_loadGenerator_works_20_sec_with_rate_of_10_000() {
        testRateAndCountOfInvocation(20, 10_000);
    }

    @Test
    public void worker_should_be_invoked_300_000_000_times_when_loadGenerator_works_30_sec_with_rate_of_10_000_000() {
        testRateAndCountOfInvocation(30, 10_000_000);
    }

    private void testRateAndCountOfInvocation(int numOfSeconds, long rate) {
        // GIVEN
        double tolerance = 0.005;
        final AtomicLong numOfInvoked = new AtomicLong(0);
        LoadGenerator<Integer> loadGenerator = new LoadGenerator<>(new RandomIntDataSource(),
                new ConstantRateGenerator(rate), (x) -> {
                    numOfInvoked.incrementAndGet();
                });

        // WHEN
        runInBackground(() -> {
            loadGenerator.run();
        });
        wait(numOfSeconds * 1_000);

        loadGenerator.terminate();
        wait(500);

        // THEN
        Assert.assertEquals(1d * numOfSeconds * rate, numOfInvoked.get(), numOfSeconds * rate * tolerance);
    }

    private void runInBackground(Runnable target) {
        Thread t = new Thread(target);
        t.start();
    }

    private void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
