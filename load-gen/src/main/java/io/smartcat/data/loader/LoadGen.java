package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadGen {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGen.class);

    private ImpulseGenerator impulseGenerator;

    private Timer impulseTimer;

    public LoadGen() {

        impulseGenerator = new ImpulseGenerator.ImpulseGeneratorBuilder().withMetrics(true).build();

        impulseTimer = new Timer("impulse-generator-timer");
        impulseTimer.scheduleAtFixedRate(new Counter(), 0, 1000);

        impulseGenerator.start(10000);
    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} impulses", impulseGenerator.getImpulseCount());
        }
    }

}
