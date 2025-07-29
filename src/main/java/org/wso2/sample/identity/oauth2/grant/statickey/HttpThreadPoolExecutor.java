package org.wso2.sample.identity.oauth2.grant.statickey;

import java.util.concurrent.*;

public class HttpThreadPoolExecutor {
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static Future<?> submitTask(Runnable task) {
        return executorService.submit(task);
    }

    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(HttpThreadPoolExecutor::shutdown));
    }
}