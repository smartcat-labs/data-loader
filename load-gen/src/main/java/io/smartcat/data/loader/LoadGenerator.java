package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerator.class);

    private ImpulseGenerator impulseGenerator;

    private Timer impulseTimer;

    public LoadGenerator() {

        impulseGenerator = new ImpulseGenerator.ImpulseGeneratorBuilder().withMetrics(true).build();

        impulseTimer = new Timer("impulse-generator-timer");
        impulseTimer.scheduleAtFixedRate(new Counter(), 0, 1000);
    }

    public void start(double targetRate) {
        impulseGenerator.start(targetRate);
    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} impulses", impulseGenerator.getImpulseCount());
        }
    }

}
