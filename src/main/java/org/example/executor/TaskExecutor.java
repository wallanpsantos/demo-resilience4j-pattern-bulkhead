package org.example.executor;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.OrderService;
import org.example.service.ReportService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class TaskExecutor {

    private final OrderService orderService;
    private final ReportService reportService;

    public void executeOrderTask(ThreadPoolBulkhead bulkhead, int orderId) {
        try {
            Supplier<String> task = () -> orderService.processOrder(orderId);
            executeTask(bulkhead, task, orderId, "Pedido", 3,
                    exception -> orderService.processOrderFallback(orderId, exception));
        } catch (BulkheadFullException e) {
            handleImmediateRejection(orderId, "Pedido", e,
                    exception -> orderService.processOrderFallback(orderId, exception));
        } catch (Exception e) {
            log.error("[Pedido {}] Erro inesperado: {}", orderId, e.getMessage());
        }
    }

    public void executeReportTask(ThreadPoolBulkhead bulkhead, int reportId) {
        try {
            Supplier<String> task = () -> reportService.generateReport(reportId);
            executeTask(bulkhead, task, reportId, "Relatório", 5,
                    exception -> reportService.generateReportFallback(reportId, exception));
        } catch (BulkheadFullException e) {
            handleImmediateRejection(reportId, "Relatório", e,
                    exception -> reportService.generateReportFallback(reportId, exception));
        } catch (Exception e) {
            log.error("[Relatório {}] Erro inesperado: {}", reportId, e.getMessage());
        }
    }

    private void executeTask(ThreadPoolBulkhead bulkhead, Supplier<String> task, int id,
                             String taskType, int timeoutSeconds,
                             java.util.function.Function<Throwable, String> fallbackFunction) {
        // Correção: converter Supplier para Callable
        CompletableFuture<String> future = bulkhead.submit(task::get).toCompletableFuture();

        future.orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        handleTaskException(id, taskType, exception, fallbackFunction);
                    } else {
                        log.info("[{} {}] ✓ Resultado: {}", taskType, id, result);
                    }
                });
    }

    private void handleTaskException(int id, String taskType, Throwable exception,
                                     java.util.function.Function<Throwable, String> fallbackFunction) {
        if (exception.getCause() instanceof BulkheadFullException) {
            log.error("[{} {}] REJEITADO - Bulkhead cheio: {}",
                    taskType, id, exception.getCause().getMessage());
            applyFallback(id, taskType, exception, fallbackFunction);
        } else {
            log.error("[{} {}] Falhou: {}", taskType, id, exception.getMessage());
            applyFallback(id, taskType, exception, fallbackFunction);
        }
    }

    private void handleImmediateRejection(int id, String taskType, BulkheadFullException exception,
                                          java.util.function.Function<Throwable, String> fallbackFunction) {
        log.error("[{} {}] REJEITADO IMEDIATAMENTE - Bulkhead cheio: {}",
                taskType, id, exception.getMessage());
        applyFallback(id, taskType, exception, fallbackFunction);
    }

    private void applyFallback(int id, String taskType, Throwable exception,
                               java.util.function.Function<Throwable, String> fallbackFunction) {
        String fallbackResult = fallbackFunction.apply(exception);
        log.info("[{} {}] Fallback aplicado: {}", taskType, id, fallbackResult);
    }
}