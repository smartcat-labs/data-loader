package io.smartcat.data.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.smartcat.data.loader.api.DataSource;

public class DataCollectorTest {

    @Test
    public void should_initialize() throws InterruptedException {
        int queueSize = 100;
        TestDataSource testDataSource = new TestDataSource();
        DataCollector<Integer> collector = new DataCollector<>(testDataSource, queueSize);

        collector.start();
        assertTrue(collector.queueSize() > queueSize * 0.75);
        collector.stop();
    }

    @Test
    public void should_deplete_data_queue() throws InterruptedException {
        int queueSize = 10;
        TestDataSource testDataSource = new TestDataSource();
        DataCollector<Integer> collector = new DataCollector<>(testDataSource, queueSize);

        collector.start();
        collector.stop();

        int count = 0;
        while (collector.queueSize() > 0) {
            collector.poll();
            count++;
        }

        assertEquals(collector.queueSize(), 0);
        assertEquals(count, 10);
    }

    @Test
    public void should_deplete_data_when_data_source_empty() throws InterruptedException {
        int queueSize = 10;
        TestDataSource testDataSource = new TestDataSource();
        DataCollector<Integer> collector = new DataCollector<>(testDataSource, queueSize);

        collector.start();
        testDataSource.setHasNext(false);

        int size = collector.queueSize();
        for (int i = 0; i < size; i++) {
            collector.poll();
        }

        assertEquals(collector.queueSize(), 0);

        collector.stop();
    }

    @Test
    public void should_repopulate_queue_when_data_available() throws InterruptedException {
        int queueSize = 10;
        TestDataSource testDataSource = new TestDataSource();
        DataCollector<Integer> collector = new DataCollector<>(testDataSource, queueSize);

        collector.start();
        testDataSource.setHasNext(false);

        int size = collector.queueSize();
        for (int i = 0; i < size; i++) {
            collector.poll();
        }

        assertEquals(collector.queueSize(), 0);

        CountDownLatch latch = new CountDownLatch(queueSize);
        testDataSource.setCountDownLatch(latch);
        testDataSource.setHasNext(true);
        boolean wait = latch.await(100, TimeUnit.MILLISECONDS);

        assertTrue(wait);
        assertEquals(collector.queueSize(), queueSize);

        collector.stop();
    }

    public class TestDataSource implements DataSource<Integer> {

        private boolean hasNext = true;
        private CountDownLatch latch;

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public void setCountDownLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Integer next() {
            if (latch != null) {
                latch.countDown();
            }
            return 1;
        }
    }
}
