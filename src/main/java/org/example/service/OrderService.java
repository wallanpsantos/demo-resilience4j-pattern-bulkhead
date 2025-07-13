package org.example.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderService {

    private static final int ORDER_PROCESSING_TIME_MS = 500;

    public String processOrder(int orderId) {
        try {
            logProcessingStart(orderId);
            simulateProcessingTime();
            logProcessingComplete(orderId);
            return buildSuccessMessage(orderId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return buildInterruptedMessage(orderId);
        }
    }

    public String processOrderFallback(int orderId, Throwable exception) {
        log.warn("[Pedido {}] Usando fallback devido a: {}", orderId, exception.getMessage());
        return "Pedido " + orderId + " será processado posteriormente (fallback)";
    }

    private void logProcessingStart(int orderId) {
        log.info("[Pedido {}] Iniciando processamento... [Thread: {}]",
                orderId, Thread.currentThread().getName());
    }

    private void simulateProcessingTime() throws InterruptedException {
        Thread.sleep(ORDER_PROCESSING_TIME_MS);
    }

    private void logProcessingComplete(int orderId) {
        log.info("[Pedido {}] Processamento concluído.", orderId);
    }

    private String buildSuccessMessage(int orderId) {
        return "Pedido " + orderId + " processado com sucesso";
    }

    private String buildInterruptedMessage(int orderId) {
        return "Processamento do pedido " + orderId + " interrompido";
    }
}