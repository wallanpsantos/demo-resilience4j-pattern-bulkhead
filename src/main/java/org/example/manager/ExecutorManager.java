package org.example.manager;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ExecutorManager {

    private static final int THREAD_POOL_SIZE = 20;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ExecutorService executor;

    public ExecutorManager() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void submit(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logShutdownWarning();
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logShutdownInterruption();
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void logShutdownWarning() {
        log.warn("Executor não finalizou no tempo esperado, forçando shutdown...");
    }

    private void logShutdownInterruption() {
        log.warn("Shutdown interrompido, forçando shutdown imediato...");
    }
}