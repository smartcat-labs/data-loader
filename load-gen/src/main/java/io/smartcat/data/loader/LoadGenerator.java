package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.DataSource;
import io.smartcat.data.loader.api.WorkTask;
import io.smartcat.data.loader.tokenbuket.FixedRateRefillStrategy;
import io.smartcat.data.loader.tokenbuket.RefillStrategy;
import io.smartcat.data.loader.tokenbuket.SleepStrategies;
import io.smartcat.data.loader.tokenbuket.SleepStrategy;
import io.smartcat.data.loader.tokenbuket.TokenBucket;
import io.smartcat.data.loader.util.NoOpDataSource;
import io.smartcat.data.loader.util.NoOpWorkTask;

/**
 * Load generator used to execute work tasks with data from provided data source.
 */
public class LoadGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerator.class);

    private final DataCollector dataCollector;

    private final TokenBucket tokenBucket;

    private final PulseGenerator pulseGenerator;

    private Timer metricsTimer;

    private LoadGenerator(Builder builder) {
        this.dataCollector = new DataCollector(builder.dataSource, (int) (builder.targetRate * 10));
        this.tokenBucket = new TokenBucket(0, builder.refillStrategy, builder.sleepStrategy);
        this.pulseGenerator = new PulseGenerator(dataCollector, tokenBucket, builder.workTask, builder.collectMetrics);

        this.metricsTimer = new Timer("pulse-generator-timer");
        this.metricsTimer.scheduleAtFixedRate(new MetricsLogger(), 0, 1000);
    }

    /**
     * Start load generator.
     */
    public void start() {
        pulseGenerator.start();
    }

    /**
     * Metrics logger timer task.
     */
    private class MetricsLogger extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} pulses", pulseGenerator.getPulseCount());
        }
    }

    /**
     * LoadGenerator builder class.
     */
    public static class Builder {

        private int targetRate = 1000;
        private boolean collectMetrics = true;
        private WorkTask workTask = new NoOpWorkTask();
        private DataSource dataSource = new NoOpDataSource();
        private RefillStrategy refillStrategy;
        private SleepStrategy sleepStrategy = SleepStrategies.nanosecondSleepStrategy(1);

        /**
         * Set target rate per second.
         *
         * @param targetRate target rate
         * @return builder
         */
        public Builder withTargetRate(int targetRate) {
            this.targetRate = targetRate;
            return this;
        }

        /**
         * Set this to true if metrics should be collected.
         *
         * @param collectMetrics collect metrics
         * @return builder
         */
        public Builder withCollectMetrics(boolean collectMetrics) {
            this.collectMetrics = collectMetrics;
            return this;
        }

        /**
         * WorkTask interface implementation. This is what load generator executes at a given rate.
         *
         * @param workTask work task implementation
         * @return builder
         */
        public Builder withWorkTask(WorkTask workTask) {
            this.workTask = workTask;
            return this;
        }

        /**
         * DataSource interface implementation. Provides data source for all executed tasks.
         *
         * @param dataSource data source implementation
         * @return builder
         */
        public Builder withDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * Refill strategy implementation for generating token bucket tokens.
         * Default implementation is {@code io.smartcat.data.loader.tokenbuket.FixedRateRefillStrategy}.
         *
         * @param refillStrategy refill strategy implementation
         * @return builder
         */
        public Builder withRefillStrategy(RefillStrategy refillStrategy) {
            this.refillStrategy = refillStrategy;
            return this;
        }

        /**
         * Sleep strategy implementation for inserting sleep in token bucket algorithm. Default implementation is
         * {@code io.smartcat.data.loader.tokenbuket.SleepStrategies.NANOSECOND_SLEEP_STRATEGY}.
         *
         * @param sleepStrategy sleep strategy implementation
         * @return builder
         */
        public Builder withSleepStrategy(SleepStrategy sleepStrategy) {
            this.sleepStrategy = sleepStrategy;
            return this;
        }

        /**
         * Build load generator with provided parameters.
         *
         * @return load generator instance
         */
        public LoadGenerator build() {
            if (this.refillStrategy == null) {
                LOGGER.info("Defaulting to FixedRateRefillStrategy.");
                this.refillStrategy = new FixedRateRefillStrategy(this.targetRate, 1, TimeUnit.SECONDS);
            }

            return new LoadGenerator(this);
        }
    }

}
