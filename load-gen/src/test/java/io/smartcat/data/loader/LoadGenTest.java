package io.smartcat.data.loader;

import org.junit.Test;

public class LoadGenTest {

    @Test
    public void should_initialize() {
        final LoadGen loadGen = new LoadGen();

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}