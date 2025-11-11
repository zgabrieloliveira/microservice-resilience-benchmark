package br.ufu.gabrieloliveira.ms_payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    // Usaremos POST para simular uma ação real
    @PostMapping("/pay")
    public String pay() {
        // No futuro, aqui ficará a injeção de falha (o "sleep")
        return "{\"status\": \"PAGAMENTO_OK\"}";
    }
}
