package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load generator used to execute work tasks with data from provided data source.
 */
public class LoadGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerator.class);

    private PulseGenerator pulseGenerator;

    private Timer pulseTimer;

    /**
     * Constructor.
     */
    public LoadGenerator() {

        pulseGenerator = new PulseGenerator.PulseGeneratorBuilder().withTargetRate(1000).withMetrics(true).build();

        pulseTimer = new Timer("pulse-generator-timer");
        pulseTimer.scheduleAtFixedRate(new Counter(), 0, 1000);
    }

    /**
     * Start load generator.
     */
    public void start() {
        pulseGenerator.start();
    }

    /**
     * Pulse metrics counter.
     */
    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} pulses", pulseGenerator.getPulseCount());
        }
    }

}
