package io.smartcat.ranger.load.generator.datasource;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.ranger.load.generator.api.DataSource;

/**
 * Data source buffer which polls from specified delegate data source from different thread. Blocks polling at the
 * beginning until buffer is filled to <code>initialBufferFullness</code>.
 * <p>
 * <b>Note:</b>Only time independent {@link DataSource} implementations should be used with this implementation.
 * For more info read the next paragraph.
 * </p>
 * <p>
 * Buffered data source breaks {@link DataSource} interface since <code>delegate</code> data source is called
 * with time value of <code>0</code>. This is due to buffer implementation which fills buffer from another thread.
 * Since <code>delegate.getNext(...)</code> is invoked from another thread, it does not have information on time
 * component, hence cannot invoke <code>delegate.getNext(...)</code> with proper value. If you are using
 * time dependent {@link DataSource} implementation, it will not work together with this implementation.
 * </p>
 *
 * @param <T> Type of the data provided by data source implementation.
 */
public class BufferedDataSource<T> implements DataSource<T>, AutoCloseable {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final double DEFAULT_INITIAL_BUFFER_FULLNESS = 0.75;

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedDataSource.class);

    private final Object bufferFilled = new Object();
    private final DataSource<T> delegate;
    private final int bufferSize;
    private final Queue<T> buffer;
    private final double initialBufferFullness;

    private AtomicBoolean terminateThread = new AtomicBoolean(false);
    private Thread thread;

    /**
     * Constructs a buffered data source with specified <code>delegate</code> data source. <code>bufferSize</code> is
     * set to 1024, <code>initialBufferFullness</code> is set to 75%. Blocks polling at the beginning until buffer is
     * filled to <code>initialBufferFullness</code>.
     *
     * @param delegate Data source which will be used to fill buffer.
     */
    public BufferedDataSource(DataSource<T> delegate) {
        this(delegate, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Constructs a buffered data source with specified <code>delegate</code> data source and <code>bufferSize</code>.
     * <code>initialBufferFullness</code> is set to 75%. Blocks polling at the beginning until buffer is filled to
     * <code>initialBufferFullness</code>.
     *
     * @param delegate Data source which will be used to fill buffer.
     * @param bufferSize Size of the buffer.
     */
    public BufferedDataSource(DataSource<T> delegate, int bufferSize) {
        this(delegate, bufferSize, DEFAULT_INITIAL_BUFFER_FULLNESS);
    }

    /**
     * Constructs a buffered data source with specified <code>delegate</code> data source, <code>bufferSize</code> and
     * <code>initialBufferFullness</code>. Blocks polling at the beginning until buffer is filled to
     * <code>initialBufferFullness</code>.
     *
     * @param delegate Data source which will be used to fill buffer.
     * @param bufferSize Size of the buffer.
     * @param initialBufferFullness Initial fullness of buffer.
     */
    public BufferedDataSource(DataSource<T> delegate, int bufferSize, double initialBufferFullness) {
        this.delegate = delegate;
        this.bufferSize = bufferSize;
        this.buffer = new LinkedBlockingQueue<>(bufferSize);
        this.initialBufferFullness = initialBufferFullness;
        fillBuffer();
    }

    @Override
    public boolean hasNext(long time) {
        return !buffer.isEmpty() || delegate.hasNext(time);
    }

    @Override
    public T getNext(long time) {
        try {
            bufferFilled.wait();
            T result = buffer.poll();
            LOGGER.trace("Polled value from buffer: {}", result);
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        terminateThread.set(false);
    }

    private void fillBuffer() {
        thread = new Thread(() -> {
            LOGGER.trace("Filling buffer to initial buffer size...");
            while (buffer.size() < bufferSize * initialBufferFullness) {
                buffer.offer(delegate.getNext(0));
            }
            LOGGER.trace("Buffer filled to initial buffer size of {} elements", buffer.size());
            bufferFilled.notify();
            while (!terminateThread.get()) {
                buffer.offer(delegate.getNext(0));
            }
        });
        thread.start();
    }
}
