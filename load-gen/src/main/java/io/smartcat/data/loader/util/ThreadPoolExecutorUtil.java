package io.smartcat.data.loader.util;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolExecutor utility class.
 */
public class ThreadPoolExecutorUtil {

    private ThreadPoolExecutorUtil() {

    }

    /**
     * This method fills thread pool executor up to a core pool size with instances of provided {@code
     * java.lang.Runnable} implementation.
     *
     * @param threadPoolExecutor thread pool executor
     * @param runnable runnable implementation
     */
    public static void fillThreadPool(ThreadPoolExecutor threadPoolExecutor, Runnable runnable) {
        while (threadPoolExecutor.getPoolSize() < threadPoolExecutor.getCorePoolSize()) {
            threadPoolExecutor.submit(runnable);
        }
    }

}
