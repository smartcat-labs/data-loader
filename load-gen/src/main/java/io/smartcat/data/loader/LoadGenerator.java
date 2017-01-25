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

    private ImpulseGenerator impulseGenerator;

    private Timer impulseTimer;

    /**
     * Constructor.
     */
    public LoadGenerator() {

        impulseGenerator = new ImpulseGenerator.ImpulseGeneratorBuilder().withMetrics(true).build();

        impulseTimer = new Timer("impulse-generator-timer");
        impulseTimer.scheduleAtFixedRate(new Counter(), 0, 1000);
    }

    /**
     * Start load generator.
     *
     * @param targetRate target load rate
     */
    public void start(double targetRate) {
        impulseGenerator.start(targetRate);
    }

    /**
     * Impulse metrics counter.
     */
    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} impulses", impulseGenerator.getImpulseCount());
        }
    }

}
