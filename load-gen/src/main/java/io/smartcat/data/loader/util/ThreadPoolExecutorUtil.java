package io.smartcat.data.loader.util;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolExecutor utility class.
 */
public class ThreadPoolExecutorUtil {

    public static void fillThreadPool(ThreadPoolExecutor threadPoolExecutor, Runnable task) {
        while (threadPoolExecutor.getPoolSize() < threadPoolExecutor.getCorePoolSize()) {
            threadPoolExecutor.submit(task);
        }
    }

}
