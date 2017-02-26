package io.smartcat.data.loader;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoadGeneratorTest {

    @Test
    public void should_initialize() {
        final LoadGenerator loadGenerator = new LoadGenerator.Builder().withTargetRate(10000).withCollectMetrics(true)
                .build();
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
