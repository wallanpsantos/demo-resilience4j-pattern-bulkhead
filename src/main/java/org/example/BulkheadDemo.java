package org.example;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.extern.slf4j.Slf4j;
import org.example.config.BulkheadConfig;
import org.example.executor.TaskExecutor;
import org.example.listener.BulkheadEventListener;
import org.example.manager.ExecutorManager;
import org.example.reporter.StatisticsReporter;
import org.example.service.OrderService;
import org.example.service.ReportService;

@Slf4j
public class BulkheadDemo {

    private static final int REPORT_REQUESTS_COUNT = 8;
    private static final int ORDER_REQUESTS_COUNT = 18;
    private static final int REPORT_DELAY_MS = 1000;
    private static final int FINAL_WAIT_MS = 10000;

    private final OrderService orderService;
    private final ReportService reportService;
    private final TaskExecutor taskExecutor;
    private final StatisticsReporter statisticsReporter;
    private final ExecutorManager executorManager;

    public BulkheadDemo() {
        this.orderService = new OrderService();
        this.reportService = new ReportService();
        this.taskExecutor = new TaskExecutor(orderService, reportService);
        this.statisticsReporter = new StatisticsReporter();
        this.executorManager = new ExecutorManager();
    }

    public static void main(String[] args) throws Exception {
        new BulkheadDemo().runDemo();
    }

    public void runDemo() throws Exception {
        ThreadPoolBulkhead orderBulkhead = BulkheadConfig.createOrderBulkhead();
        ThreadPoolBulkhead reportBulkhead = BulkheadConfig.createReportBulkhead();

        BulkheadEventListener orderListener = new BulkheadEventListener("Pedidos");
        BulkheadEventListener reportListener = new BulkheadEventListener("Relatórios");

        orderListener.attachToOrderBulkhead(orderBulkhead);
        reportListener.attachToReportBulkhead(reportBulkhead);

        try {
            startSimulation();
            executeReportRequests(reportBulkhead);
            waitBetweenRequestTypes();
            executeOrderRequests(orderBulkhead);
            waitForCompletion();
            printStatistics(orderBulkhead, reportBulkhead, orderListener, reportListener);
        } catch (InterruptedException e) {
            handleInterruption(e);
        } finally {
            cleanup(orderBulkhead, reportBulkhead);
        }
    }

    private void startSimulation() {
        log.info("\n=== INICIANDO SIMULAÇÃO DE BULKHEAD ===\n");
    }

    private void executeReportRequests(ThreadPoolBulkhead reportBulkhead) {
        log.info("--- Disparando {} requisições de relatórios (Bulkhead deve rejeitar algumas) ---\n",
                REPORT_REQUESTS_COUNT);

        for (int i = 1; i <= REPORT_REQUESTS_COUNT; i++) {
            final int reportId = i;
            executorManager.submit(() -> taskExecutor.executeReportTask(reportBulkhead, reportId));
        }
    }

    private void waitBetweenRequestTypes() throws InterruptedException {
        Thread.sleep(REPORT_DELAY_MS);
    }

    private void executeOrderRequests(ThreadPoolBulkhead orderBulkhead) {
        log.info("\n--- Disparando {} requisições de pedidos (algumas devem ser rejeitadas) ---\n",
                ORDER_REQUESTS_COUNT);

        for (int i = 1; i <= ORDER_REQUESTS_COUNT; i++) {
            final int orderId = i;
            executorManager.submit(() -> taskExecutor.executeOrderTask(orderBulkhead, orderId));
        }
    }

    private void waitForCompletion() throws InterruptedException {
        log.info("\n--- Aguardando conclusão de todas as tarefas... ---\n");
        Thread.sleep(FINAL_WAIT_MS);
    }

    private void printStatistics(ThreadPoolBulkhead orderBulkhead,
                                 ThreadPoolBulkhead reportBulkhead,
                                 BulkheadEventListener orderListener,
                                 BulkheadEventListener reportListener) {
        statisticsReporter.printFinalStatistics(orderBulkhead, reportBulkhead,
                orderListener, reportListener);
    }

    private void handleInterruption(InterruptedException e) {
        log.error("Simulação interrompida: {}", e.getMessage());
        Thread.currentThread().interrupt();
    }

    private void cleanup(ThreadPoolBulkhead orderBulkhead, ThreadPoolBulkhead reportBulkhead) throws Exception {
        executorManager.shutdown();
        orderBulkhead.close();
        reportBulkhead.close();
    }
}