package io.smartcat.data.loader;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoadGeneratorTest {

    @Test
    public void should_initialize() {
        final LoadGenerator loadGenerator = new LoadGenerator();
        loadGenerator.start(10000);

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
