package io.smartcat.data.loader;

import java.util.concurrent.TimeUnit;

import io.smartcat.data.loader.util.RandomIntDataSource;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoadGeneratorTest {

    @Test
    public void should_initialize() {
        final LoadGenerator<Integer> loadGenerator = new LoadGenerator.Builder<Integer>().withTargetRate(10000)
                .withDataSource(new RandomIntDataSource()).build();
        loadGenerator.start();

        while (true) {
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
