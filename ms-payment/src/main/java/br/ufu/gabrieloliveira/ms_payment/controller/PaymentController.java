package br.ufu.gabrieloliveira.ms_payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class PaymentController {

    private boolean simulateLatency = false;
    private boolean simulateError = false;
    private static final int LATENCY_MILLISECONDS = 3000; // 3 segundos

    @PostMapping("/pay")
    public ResponseEntity<String> pay() throws InterruptedException {

        // simulate 503 Service Unavailable
        if (simulateError) {
            return ResponseEntity.status(503)
                    .body("{\"error\": \"Simulação de falha no pagamento\"}");
        }

        // lock current thread for LATENCY_MILLISECONDS
        if (simulateLatency) {
            TimeUnit.MILLISECONDS.sleep(LATENCY_MILLISECONDS);
        }

        // happy path
        return ResponseEntity.ok("{\"status\": \"PAGAMENTO_OK\"}");
    }


    // -- EXPERIMENTAL DEBUG ENDPOINTS --

    // turn on latency simulation
    @GetMapping("/debug/latency/on")
    public ResponseEntity<String> enableLatencySimulation() {
        this.simulateLatency = true;
        return ResponseEntity.ok("Latency simulation enabled.");
    }

    // turn on error simulation
    @GetMapping("/debug/error/on")
    public ResponseEntity<String> enableErrorSimulation() {
        this.simulateError = true;
        return ResponseEntity.ok("Error simulation enabled.");
    }

    // turn off all simulations (enabling happy path)
    @GetMapping("/debug/turn-off")
    public ResponseEntity<String> disableSimulations() {
        this.simulateLatency = false;
        this.simulateError = false;
        return ResponseEntity.ok("All simulations disabled.");
    }

}
