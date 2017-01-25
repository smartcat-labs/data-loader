package io.smartcat.data.loader;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.DataSource;

/**
 * Data collector implementation. Used to take care of data queue for load generator.
 */
public class DataCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCollector.class);

    private static final int DEFAULT_DATA_QUEUE_CAPACITY = 1024;

    private DataSource<Integer> dataSource;

    private LinkedBlockingQueue<Integer> dataQueue;

    private Thread dataQueueThread;

    private int dataQueueCapacity;

    /**
     * Constructor.
     *
     * @param dataSource Data source
     */
    public DataCollector(DataSource dataSource) {
        this(dataSource, DEFAULT_DATA_QUEUE_CAPACITY);
    }

    /**
     * Constructor.
     *
     * @param dataSource        Data source
     * @param dataQueueCapacity Data queue capacity
     */
    public DataCollector(DataSource dataSource, int dataQueueCapacity) {
        this.dataSource = dataSource;
        this.dataQueueCapacity = dataQueueCapacity;

        this.dataQueue = new LinkedBlockingQueue<>(dataQueueCapacity);

        this.dataQueueThread = new Thread(new Collector());
    }

    /**
     * Data queue size.
     *
     * @return data queue size
     */
    public int queueSize() {
        return this.dataQueue.size();
    }

    /**
     * Poll for head item.
     *
     * @return Head item in queue
     */
    public Integer poll() {
        return this.dataQueue.poll();
    }

    /**
     * Start data collector. Will initialize data collection thread and prime data queue to 75% of its capacity.
     */
    public void start() {
        this.dataQueueThread.start();

        LOGGER.debug("Priming data queue from data source to 75% of capacity.");
        while (dataQueue.size() < (dataQueueCapacity * 0.75)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("Error waiting to populate data queue.", e);
            }
        }
        LOGGER.debug("Finished priming data queue.");
    }

    /**
     * Stop data collector.
     *
     * @throws InterruptedException Interrupted exception
     */
    public void stop() throws InterruptedException {
        this.dataQueueThread.join();
    }

    /**
     * Data collector thread collecting data from data source and pushing it into data queue.
     */
    private class Collector implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (dataSource.hasNext() && dataQueue.size() < dataQueueCapacity) {
                    dataQueue.offer(dataSource.next());
                } else {
                    try {
                        Thread.sleep(0, 1000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error waiting for data source to be available.", e);
                    }
                }
            }
        }
    }
}
