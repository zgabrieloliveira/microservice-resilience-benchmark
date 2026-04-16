package br.ufu.gabrieloliveira.ms_order_orchestrator.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    private static final String PAYMENT_SERVICE_CB = "paymentService";

    @Value("${resilience4j.retry.enabled:true}")
    private boolean retryEnabled;

    @Value("${resilience4j.cb.enabled:true}")
    private boolean cbEnabled;

    public ResponseEntity<String> callPaymentService() {
        // 1. define O QUE deve ser feito (a chamada HTTP crua)
        Supplier<ResponseEntity<String>> paymentCall = () -> {
            String response = restTemplate.postForObject(
                    "http://ms-payment/pay",
                    null,
                    String.class
            );
            return ResponseEntity.ok(response);
        };

        // 2. aplica o Retry se estiver habilitado
        if (retryEnabled) {
            Retry retryInstance = retryRegistry.retry(PAYMENT_SERVICE_CB);
            paymentCall = Retry.decorateSupplier(retryInstance, paymentCall);
        }

        // 3. aplica o Circuit Breaker se estiver habilitado
        if (cbEnabled) {
            CircuitBreaker cbInstance = circuitBreakerRegistry.circuitBreaker(PAYMENT_SERVICE_CB);
            paymentCall = CircuitBreaker.decorateSupplier(cbInstance, paymentCall);
        }

        // 4. executa a cadeia e captura falhas para acionar o Fallback
        try {
            return paymentCall.get();
        } catch (Throwable t) {
            return fallbackPayment(t);
        }
    }

    // Fallback: o que fazer se der erro final ou o circuito abrir
    public ResponseEntity<String> fallbackPayment(Throwable t) {
        String errorBody = "{\"status\": \"PAGAMENTO_FALHOU\", \"error_details\": \"" + t.getMessage() + "\"}";
        return ResponseEntity.status(503).body(errorBody);
    }

    // Métodos para alterar a arquitetura em tempo real (usados pelo SetupController)
    public void setArchitecture(boolean retry, boolean cb) {
        this.retryEnabled = retry;
        this.cbEnabled = cb;
    }
}