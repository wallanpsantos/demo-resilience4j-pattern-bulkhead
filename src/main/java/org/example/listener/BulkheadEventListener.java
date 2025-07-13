package org.example.listener;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BulkheadEventListener {

    private final AtomicInteger processedCount;
    private final AtomicInteger rejectedCount;
    private final String serviceType;

    public BulkheadEventListener(String serviceType) {
        this.serviceType = serviceType;
        this.processedCount = new AtomicInteger(0);
        this.rejectedCount = new AtomicInteger(0);
    }

    public void attachToOrderBulkhead(ThreadPoolBulkhead bulkhead) {
        bulkhead.getEventPublisher()
                .onCallPermitted(event -> logCallPermitted(bulkhead))
                .onCallRejected(event -> logCallRejected())
                .onCallFinished(event -> logCallFinished());
    }

    public void attachToReportBulkhead(ThreadPoolBulkhead bulkhead) {
        bulkhead.getEventPublisher()
                .onCallPermitted(event -> logCallPermitted(bulkhead))
                .onCallRejected(event -> logCallRejected())
                .onCallFinished(event -> logCallFinished());
    }

    private void logCallPermitted(ThreadPoolBulkhead bulkhead) {
        log.info("[{}] Chamada PERMITIDA - Pool size: {}",
                serviceType, bulkhead.getMetrics().getThreadPoolSize());
    }

    private void logCallRejected() {
        log.warn("[{}] Chamada REJEITADA! Bulkhead está cheio.", serviceType);
        rejectedCount.incrementAndGet();
    }

    private void logCallFinished() {
        log.info("[{}] Chamada FINALIZADA", serviceType);
        processedCount.incrementAndGet();
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

    public int getRejectedCount() {
        return rejectedCount.get();
    }

    public double getSuccessRate() {
        int total = processedCount.get() + rejectedCount.get();
        return total > 0 ? (processedCount.get() * 100.0) / total : 0.0;
    }
}