package br.ufu.gabrieloliveira.ms_order_orchestrator.controller;

import br.ufu.gabrieloliveira.ms_order_orchestrator.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
public class SetupController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/resilience")
    public ResponseEntity<String> configureResilience(
            @RequestParam boolean retry,
            @RequestParam boolean cb) {

        paymentService.setArchitecture(retry, cb);

        String config = String.format("Arquitetura de Teste Atualizada -> Retry: %b | CircuitBreaker: %b", retry, cb);
        System.out.println(config);

        return ResponseEntity.ok(config);
    }
}