package io.smartcat.ranger.load.generator.util;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

public class LinkedEvictingBlockingQueueTest {

    @Test(timeout = 500)
    public void take_should_block_thread_until_element_is_available_in_queue() throws InterruptedException {
        // GIVEN
        LinkedEvictingBlockingQueue<Integer> queue = new LinkedEvictingBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        newStartedThread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
            }
            queue.put(2);
        });

        // WHEN
        countDownLatch.countDown();
        int val = queue.take();

        // THEN
        Assert.assertEquals(2, val);
    }

    @Test(timeout = 2000)
    public void should_not_take_value_from_queue_when_value_is_not_put() throws InterruptedException {
        // GIVEN
        LinkedEvictingBlockingQueue<Integer> queue = new LinkedEvictingBlockingQueue<>();
        Thread takeFromQueueThread = newStartedThread(() -> {
            try {
                queue.take();
            } catch (InterruptedException e) {
            }
        });

        // THEN
        Thread.sleep(1000);
        Assert.assertTrue(takeFromQueueThread.isAlive());
        takeFromQueueThread.interrupt();
    }

    @Test(timeout = 500)
    public void put_should_drop_element_from_head_when_queue_is_full_and_dropFromHead_is_true()
            throws InterruptedException {
        // GIVEN
        LinkedEvictingBlockingQueue<Integer> queue = new LinkedEvictingBlockingQueue<>(true, 2);
        queue.put(1);
        queue.put(2);

        // WHEN
        queue.put(3);

        // THEN
        Assert.assertEquals(new Integer(2), queue.take());
        Assert.assertEquals(new Integer(3), queue.take());
    }

    @Test(timeout = 500)
    public void put_should_drop_element_from_tail_when_queue_is_full_and_dropFromHead_is_false()
            throws InterruptedException {
        // GIVEN
        LinkedEvictingBlockingQueue<Integer> queue = new LinkedEvictingBlockingQueue<>(false, 2);
        queue.put(1);
        queue.put(2);

        // WHEN
        queue.put(3);

        // THEN
        Assert.assertEquals(new Integer(1), queue.take());
        Assert.assertEquals(new Integer(3), queue.take());
    }

    private Thread newStartedThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        return t;
    }
}
