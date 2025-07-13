package org.example.reporter;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.extern.slf4j.Slf4j;
import org.example.listener.BulkheadEventListener;

@Slf4j
public class StatisticsReporter {

    public void printFinalStatistics(ThreadPoolBulkhead orderBulkhead,
                                     ThreadPoolBulkhead reportBulkhead,
                                     BulkheadEventListener orderListener,
                                     BulkheadEventListener reportListener) {
        log.info("\n=== ESTATÍSTICAS FINAIS ===");

        printOrderStatistics(orderBulkhead, orderListener);
        printReportStatistics(reportBulkhead, reportListener);
        printBulkheadBenefits();
    }

    private void printOrderStatistics(ThreadPoolBulkhead bulkhead, BulkheadEventListener listener) {
        log.info("\n📊 BULKHEAD DE PEDIDOS:");
        log.info("- Pedidos processados: {}", listener.getProcessedCount());
        log.info("- Pedidos rejeitados: {}", listener.getRejectedCount());
        log.info("- Taxa de sucesso: {}%", String.format("%.1f", listener.getSuccessRate()));
        log.info("- Threads no pool: {}", bulkhead.getMetrics().getThreadPoolSize());
        log.info("- Threads ativas: {}", bulkhead.getMetrics().getActiveThreadCount());
    }

    private void printReportStatistics(ThreadPoolBulkhead bulkhead, BulkheadEventListener listener) {
        log.info("\n📊 BULKHEAD DE RELATÓRIOS:");
        log.info("- Relatórios processados: {}", listener.getProcessedCount());
        log.info("- Relatórios rejeitados: {}", listener.getRejectedCount());
        log.info("- Taxa de sucesso: {}%", String.format("%.1f", listener.getSuccessRate()));
        log.info("- Threads no pool: {}", bulkhead.getMetrics().getThreadPoolSize());
        log.info("- Threads ativas: {}", bulkhead.getMetrics().getActiveThreadCount());
    }

    private void printBulkheadBenefits() {
        log.info("\n✅ BENEFÍCIOS DO BULKHEAD DEMONSTRADOS:");
        log.info("- Isolamento de recursos: Relatórios lentos não afetaram o processamento de pedidos");
        log.info("- Controle de sobrecarga: Requisições em excesso foram rejeitadas de forma controlada");
        log.info("- Observabilidade: Métricas detalhadas sobre o uso dos recursos");
        log.info("- Fallback: Degradação graceful quando recursos estão indisponíveis");
    }
}