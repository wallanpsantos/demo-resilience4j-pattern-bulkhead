package org.example.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportService {

    private static final int REPORT_GENERATION_TIME_MS = 2000;

    public String generateReport(int reportId) {
        try {
            logGenerationStart(reportId);
            simulateGenerationTime();
            logGenerationComplete(reportId);
            return buildSuccessMessage(reportId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return buildInterruptedMessage(reportId);
        }
    }

    public String generateReportFallback(int reportId, Throwable exception) {
        log.warn("[Relatório {}] Usando fallback devido a: {}", reportId, exception.getMessage());
        return "Relatório " + reportId + " será gerado posteriormente (fallback)";
    }

    private void logGenerationStart(int reportId) {
        log.info("[Relatório {}] Iniciando geração... [Thread: {}]",
                reportId, Thread.currentThread().getName());
    }

    private void simulateGenerationTime() throws InterruptedException {
        Thread.sleep(REPORT_GENERATION_TIME_MS);
    }

    private void logGenerationComplete(int reportId) {
        log.info("[Relatório {}] Geração concluída.", reportId);
    }

    private String buildSuccessMessage(int reportId) {
        return "Relatório " + reportId + " gerado com sucesso";
    }

    private String buildInterruptedMessage(int reportId) {
        return "Geração do relatório " + reportId + " interrompida";
    }
}