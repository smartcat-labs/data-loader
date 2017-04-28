package io.smartcat.ranger.load.generator.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.smartcat.ranger.load.generator.api.AlreadyClosedException;
import io.smartcat.ranger.load.generator.api.Worker;
import io.smartcat.ranger.load.generator.util.LinkedEvictingBlockingQueue;

/**
 * Asynchronous worker which uses queue and thread pool to schedule work for delegate worker. When queue is full and new
 * packet is received, an old packet will be dropped. <code>dropFromHead</code> parameter determines whether packet from
 * head or from tail will e dropped.
 *
 * @param <T> Type of data this worker accepts.
 */
public class AsyncWorker<T> implements Worker<T>, AutoCloseable {

    private final Consumer<WorkerMeta<T>> workerStatsGetherer;
    private final LinkedEvictingBlockingQueue<DefaultWorkerMeta> queue;
    private final ThreadPoolExecutor threadPoolExecutor;

    private boolean closed = false;

    /**
     * Constructs asynchronous worker with specified <code>delegate</code> worker and <code>queueCapacity</code>.
     * <code>workerStatsGetherer</code> is set to null consumer, <code>dropFromHead</code> is set to true,
     * <code>threadCount</code> is set to <code>Runtime.getRuntime().availableProcessors()</code>,
     * <code>threadFactory</code> is set to {@link DefaultThreadFactory}.
     *
     * @param delegate Worker which is run in thread pool and to which work is delegated.
     * @param queueCapacity Capacity of the queue used as a packet buffer, must be positive number.
     */
    public AsyncWorker(Worker<T> delegate, int queueCapacity) {
        this(delegate, queueCapacity, (meta) -> { });
    }

    /**
     * Constructs asynchronous worker with specified <code>delegate</code> worker, <code>queueCapacity</code> and
     * <code>workerStatsGetherer</code>. <code>dropFromHead</code> is set to true, <code>threadCount</code> is set to
     * <code>Runtime.getRuntime().availableProcessors()</code>, <code>threadFactory</code> is set to
     * {@link DefaultThreadFactory}.
     *
     * @param delegate Worker which is run in thread pool and to which work is delegated.
     * @param queueCapacity Capacity of the queue used as a packet buffer, must be positive number.
     * @param workerStatsGetherer Consumer which receives {@link WorkerMeta} packet information.
     */
    public AsyncWorker(Worker<T> delegate, int queueCapacity, Consumer<WorkerMeta<T>> workerStatsGetherer) {
        this(delegate, queueCapacity, workerStatsGetherer, true);
    }

    /**
     * Constructs asynchronous worker with specified <code>delegate</code> worker, <code>queueCapacity</code>,
     * <code>workerStatsGetherer</code> and <code>dropFromHead</code>. <code>threadCount</code> is set to
     * <code>Runtime.getRuntime().availableProcessors()</code>, <code>threadFactory</code> is set to
     * {@link DefaultThreadFactory}.
     *
     * @param delegate Worker which is run in thread pool and to which work is delegated.
     * @param queueCapacity Capacity of the queue used as a packet buffer, must be positive number.
     * @param workerStatsGetherer Consumer which receives {@link WorkerMeta} packet information.
     * @param dropFromHead If true, packet from head of the queue will be dropped, if false, packet from tail of the
     *            queue will be dropped.
     */
    public AsyncWorker(Worker<T> delegate, int queueCapacity, Consumer<WorkerMeta<T>> workerStatsGetherer,
            boolean dropFromHead) {
        this(delegate, queueCapacity, workerStatsGetherer, dropFromHead, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Constructs asynchronous worker with specified <code>delegate</code> worker, <code>queueCapacity</code>,
     * <code>workerStatsGetherer</code>, <code>dropFromHead</code> and <code>threadCount</code>.
     * <code>threadFactory</code> is set to {@link DefaultThreadFactory}.
     *
     * @param delegate Worker which is run in thread pool and to which work is delegated.
     * @param queueCapacity Capacity of the queue used as a packet buffer, must be positive number.
     * @param workerStatsGetherer Consumer which receives {@link WorkerMeta} packet information.
     * @param dropFromHead If true, packet from head of the queue will be dropped, if false, packet from tail of the
     *            queue will be dropped.
     * @param threadCount Number of thread to be used by thread pool, must be positive number.
     */
    public AsyncWorker(Worker<T> delegate, int queueCapacity, Consumer<WorkerMeta<T>> workerStatsGetherer,
            boolean dropFromHead, int threadCount) {
        this(delegate, queueCapacity, workerStatsGetherer, dropFromHead, threadCount, new DefaultThreadFactory());
    }

    /**
     * Constructs asynchronous worker with specified <code>delegate</code> worker, <code>queueCapacity</code>,
     * <code>workerStatsGetherer</code>, <code>dropFromHead</code>, <code>threadCount</code> and
     * <code>threadFactory</code>.
     *
     * @param delegate Worker which is run in thread pool and to which work is delegated.
     * @param queueCapacity Capacity of the queue used as a packet buffer, must be positive number.
     * @param workerStatsGetherer Consumer which receives {@link WorkerMeta} packet information.
     * @param dropFromHead If true, packet from head of the queue will be dropped, if false, packet from tail of the
     *            queue will be dropped.
     * @param threadCount Number of thread to be used by thread pool, must be positive number.
     * @param threadFactory ThreadFactory to be used in creating threads for thread pool.
     */
    public AsyncWorker(Worker<T> delegate, int queueCapacity, Consumer<WorkerMeta<T>> workerStatsGetherer,
            boolean dropFromHead, int threadCount, ThreadFactory threadFactory) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null.");
        }
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive number.");
        }
        if (workerStatsGetherer == null) {
            throw new IllegalArgumentException("Worker stats gatherer cannot be null.");
        }
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive number.");
        }
        if (threadFactory == null) {
            throw new IllegalArgumentException("Thread factory cannot be null.");
        }
        this.workerStatsGetherer = workerStatsGetherer;
        this.queue = new LinkedEvictingBlockingQueue<>(dropFromHead, queueCapacity);
        this.threadPoolExecutor = createAndInitThreadPoolExecutor(delegate, workerStatsGetherer, threadCount,
                threadFactory);
    }

    @Override
    public void accept(T t) {
        if (closed) {
            throw new AlreadyClosedException("Worker is already closed.");
        }
        DefaultWorkerMeta meta = new DefaultWorkerMeta(t);
        DefaultWorkerMeta dropped = queue.put(meta);
        if (dropped != null) {
            dropped.markAsDropped();
            workerStatsGetherer.accept(dropped);
        }
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            threadPoolExecutor.shutdownNow();
            closed = true;
        }
    }

    private ThreadPoolExecutor createAndInitThreadPoolExecutor(Worker<T> delegate,
            Consumer<WorkerMeta<T>> workerStatsGetherer, int threadCount, ThreadFactory threadFactory) {
        ThreadPoolExecutor result = new ThreadPoolExecutor(threadCount, threadCount, 10, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory);
        while (result.getPoolSize() < threadCount) {
            result.submit(() -> {
                while (true) {
                    DefaultWorkerMeta meta = queue.take();
                    meta.markAsAccepted();
                    delegate.accept(meta.getPayload());
                    meta.markAsDone();
                    workerStatsGetherer.accept(meta);
                }
            });
        }
        return result;
    }

    /**
     * Thread factory that creates normal priority, daemon threads with
     * 'async-worker-thread-&lt;number&gt;' names.
     */
    public static class DefaultThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("async-worker-thread-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }

    /**
     * Meta data on worker packet processing.
     *
     * @param <T> Type of payload this meta data contains.
     */
    public interface WorkerMeta<T> {

        /**
         * Returns payload sent to worker.
         *
         * @return payload sent to worker.
         */
        T getPayload();

        /**
         * Returns time in nanoseconds when packet was submitted to the worker thread.
         *
         * @return time in nanoseconds when packet was submitted to the worker thread.
         */
        long getTimeSubmittedInNanos();

        /**
         * Returns time in nanoseconds when packet was accepted by the worker.
         *
         * @return time in nanoseconds when packet was accepted by the worker.
         */
        long getTimeAcceptedInNanos();

        /**
         * Returns time in nanoseconds when processing was done on packet.
         *
         * @return time in nanoseconds when processing was done on packet.
         */
        long getTimeDoneInNanos();

        /**
         * Indicates whether packet is processed or dropped.
         *
         * @return True if packet was dropped, otherwise false.
         */
        boolean isDropped();
    }

    /**
     * Default implementation of {@link WorkerMeta}.
     */
    private class DefaultWorkerMeta implements WorkerMeta<T> {

        private final T payload;
        private final long timeSubmittedInNanos;
        private long timeAcceptedInNanos;
        private long timeDoneInNanos;
        private boolean dropped = false;

        public DefaultWorkerMeta(T payload) {
            this.payload = payload;
            this.timeSubmittedInNanos = now();
        }

        public void markAsAccepted() {
            timeAcceptedInNanos = now();
        }

        public void markAsDone() {
            timeDoneInNanos = now();
        }

        public void markAsDropped() {
            dropped = true;
        }

        @Override
        public T getPayload() {
            return payload;
        }

        @Override
        public long getTimeSubmittedInNanos() {
            return timeSubmittedInNanos;
        }

        @Override
        public long getTimeAcceptedInNanos() {
            return timeAcceptedInNanos;
        }

        @Override
        public long getTimeDoneInNanos() {
            return timeDoneInNanos;
        }

        @Override
        public boolean isDropped() {
            return dropped;
        }

        @Override
        public String toString() {
            return "DefaultWorkerMeta [payload=" + payload + ", timeSubmittedInNanos=" + timeSubmittedInNanos
                    + ", timeAcceptedInNanos=" + timeAcceptedInNanos + ", timeDoneInNanos=" + timeDoneInNanos
                    + ", dropped=" + dropped + "]";
        }

        private long now() {
            return System.nanoTime();
        }
    }
}
