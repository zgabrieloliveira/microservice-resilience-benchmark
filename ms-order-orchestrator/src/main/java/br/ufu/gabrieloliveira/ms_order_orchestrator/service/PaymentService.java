package br.ufu.gabrieloliveira.ms_order_orchestrator.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    // 1. nome do Circuit Breaker
    private static final String PAYMENT_SERVICE_CB = "paymentService";

    // RETRY: para falhas transientes (rápidas)
    // CIRCUIT BREAKER: para falhas persistentes (longas)

    // 2. Anota o metodo com o Circuit Breaker e aponta para um metodo de "fallback" (plano B)
    @Retry(name = "paymentService")
    @CircuitBreaker(name = PAYMENT_SERVICE_CB, fallbackMethod = "fallbackPayment")
    public ResponseEntity<String> callPaymentService() {
        String response = restTemplate.postForObject(
                "http://ms-payment/pay", //
                null,
                String.class
        );
        return ResponseEntity.ok(response);
    }

    // 3. Fallback: o que fazer se o circuito abrir?
    // (chamado automaticamente pelo Resilience4j quando o 'pay' falhar)
    public ResponseEntity<String> fallbackPayment(Throwable t) {
        String errorBody = "{\"status\": \"PAGAMENTO_FALHOU_FAIL_FAST\", \"error_details\": \"" + t.getMessage() + "\"}";
        // retorna um erro 503 (Serviço Indisponível) instantaneamente
        return ResponseEntity.status(503).body(errorBody);
    }
}