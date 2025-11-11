package br.ufu.gabrieloliveira.ms_order_orchestrator.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String PAYMENT_SERVICE_CB = "paymentService";

    // Mude o tipo de retorno para ResponseEntity<String>
    @CircuitBreaker(name = PAYMENT_SERVICE_CB, fallbackMethod = "fallbackPayment")
    public ResponseEntity<String> callPaymentService() {

        String response = restTemplate.postForObject(
                "http://payment-service/pay",
                null,
                String.class
        );
        // Em caso de sucesso, retorne 200 OK
        return ResponseEntity.ok(response);
    }

    // O Fallback AGORA RETORNA UM ERRO 503
    // Isso é o "fail-fast"
    public ResponseEntity<String> fallbackPayment(Throwable t) {
        String errorBody = "{\"status\": \"PAGAMENTO_FALHOU_FAIL_FAST\", \"error\": \"" + t.getMessage() + "\"}";

        // Retorna um erro 503 (Serviço Indisponível)
        return ResponseEntity.status(503).body(errorBody);
    }
}