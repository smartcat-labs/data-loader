package io.smartcat.data.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadGen {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGen.class);

    private ImpulseGenerator impulseGenerator;

    public LoadGen() {

        impulseGenerator = new ImpulseGenerator();
        impulseGenerator.start(10000);
        try {
            Thread.sleep(3000);
            impulseGenerator.setTargetRate(1000);
            Thread.sleep(3000);
            impulseGenerator.setTargetRate(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
