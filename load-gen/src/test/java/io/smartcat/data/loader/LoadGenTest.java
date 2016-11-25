package io.smartcat.data.loader;

import org.junit.Test;

public class LoadGenTest {

    @Test
    public void should_initialize() {
        final ImpulseGenerator impulseGenerator = new ImpulseGenerator();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}