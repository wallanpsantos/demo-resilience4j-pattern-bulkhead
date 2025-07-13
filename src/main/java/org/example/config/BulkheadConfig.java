package org.example.config;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;

import java.time.Duration;

public class BulkheadConfig {

    private static final int ORDER_MAX_THREAD_POOL_SIZE = 5;
    private static final int ORDER_CORE_THREAD_POOL_SIZE = 3;
    private static final int ORDER_QUEUE_CAPACITY = 10;

    private static final int REPORT_MAX_THREAD_POOL_SIZE = 2;
    private static final int REPORT_CORE_THREAD_POOL_SIZE = 1;
    private static final int REPORT_QUEUE_CAPACITY = 2;

    private static final Duration KEEP_ALIVE_DURATION = Duration.ofMillis(100);

    public static ThreadPoolBulkhead createOrderBulkhead() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(ORDER_MAX_THREAD_POOL_SIZE)
                .coreThreadPoolSize(ORDER_CORE_THREAD_POOL_SIZE)
                .queueCapacity(ORDER_QUEUE_CAPACITY)
                .keepAliveDuration(KEEP_ALIVE_DURATION)
                .build();

        return ThreadPoolBulkhead.of("orderBulkhead", config);
    }

    public static ThreadPoolBulkhead createReportBulkhead() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(REPORT_MAX_THREAD_POOL_SIZE)
                .coreThreadPoolSize(REPORT_CORE_THREAD_POOL_SIZE)
                .queueCapacity(REPORT_QUEUE_CAPACITY)
                .keepAliveDuration(KEEP_ALIVE_DURATION)
                .build();

        return ThreadPoolBulkhead.of("reportBulkhead", config);
    }
}