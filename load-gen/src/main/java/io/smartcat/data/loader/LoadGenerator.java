package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.DataSource;
import io.smartcat.data.loader.api.WorkTask;
import io.smartcat.data.loader.tokenbuket.FixedRateRefillStrategy;
import io.smartcat.data.loader.tokenbuket.RefillStrategy;
import io.smartcat.data.loader.tokenbuket.SleepStrategies;
import io.smartcat.data.loader.tokenbuket.SleepStrategy;
import io.smartcat.data.loader.tokenbuket.TokenBucket;
import io.smartcat.data.loader.util.NoOpWorkTask;

/**
 * Load generator used to execute work tasks with data from provided data source.
 *
 * @param <T> Type of data which will be used.
 */
public class LoadGenerator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerator.class);

    private final DataCollector<T> dataCollector;

    private final TokenBucket tokenBucket;

    private final PulseGenerator<T> pulseGenerator;

    private Timer metricsTimer;

    private LoadGenerator(Builder<T> builder) {
        this.dataCollector = new DataCollector<>(builder.dataSource, builder.targetRate * 10);
        this.tokenBucket = new TokenBucket(0, builder.refillStrategy, builder.sleepStrategy);
        this.pulseGenerator = new PulseGenerator<>(dataCollector, tokenBucket, builder.workTask,
                builder.collectMetrics);

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
     *
     * @param <T> Type of load generator to build.
     */
    public static class Builder<T> {

        private int targetRate = 0;
        private Boolean collectMetrics = null;
        private WorkTask<T> workTask = null;
        private DataSource<T> dataSource;
        private RefillStrategy refillStrategy;
        private SleepStrategy sleepStrategy = null;

        /**
         * Set target rate per second.
         *
         * @param targetRate target rate
         * @return builder
         */
        public Builder<T> withTargetRate(int targetRate) {
            this.targetRate = targetRate;
            return this;
        }

        /**
         * Set this to true if metrics should be collected.
         *
         * @param collectMetrics collect metrics
         * @return builder
         */
        public Builder<T> withCollectMetrics(boolean collectMetrics) {
            this.collectMetrics = collectMetrics;
            return this;
        }

        /**
         * WorkTask interface implementation. This is what load generator executes at a given rate.
         *
         * @param workTask work task implementation
         * @return builder
         */
        public Builder<T> withWorkTask(WorkTask<T> workTask) {
            this.workTask = workTask;
            return this;
        }

        /**
         * DataSource interface implementation. Provides data source for all executed tasks.
         *
         * @param dataSource data source implementation
         * @return builder
         */
        public Builder<T> withDataSource(DataSource<T> dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * Refill strategy implementation for generating token bucket tokens. Default implementation is
         * {@code io.smartcat.data.loader.tokenbuket.FixedRateRefillStrategy}.
         *
         * @param refillStrategy refill strategy implementation
         * @return builder
         */
        public Builder<T> withRefillStrategy(RefillStrategy refillStrategy) {
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
        public Builder<T> withSleepStrategy(SleepStrategy sleepStrategy) {
            this.sleepStrategy = sleepStrategy;
            return this;
        }

        /**
         * Build load generator with provided parameters.
         *
         * @return load generator instance
         */
        public LoadGenerator<T> build() {
            checkAndSetDefaultValues();
            return new LoadGenerator<>(this);
        }

        private void checkAndSetDefaultValues() {
            if (targetRate < 1) {
                throw new IllegalStateException("Target rate is not set.");
            }
            if (dataSource == null) {
                throw new IllegalStateException("Data source is not set.");
            }
            if (collectMetrics == null) {
                LOGGER.info("DEFAULT: Collect metrics set to true.");
                this.collectMetrics = true;
            }
            if (workTask == null) {
                LOGGER.info("DEFAULT: Work task set to NoOpWorkTask.");
                this.workTask = new NoOpWorkTask<>();
            }
            if (refillStrategy == null) {
                LOGGER.info("DEFAULT: Refill strategy set to FixedRateRefillStrategy.");
                this.refillStrategy = new FixedRateRefillStrategy(targetRate);
            }
            if (sleepStrategy == null) {
                LOGGER.info("DEFAULT: Sleep strategy set to nanosecond sleep strategy.");
                this.sleepStrategy = SleepStrategies.nanosecondSleepStrategy(1);
            }
        }
    }
}
