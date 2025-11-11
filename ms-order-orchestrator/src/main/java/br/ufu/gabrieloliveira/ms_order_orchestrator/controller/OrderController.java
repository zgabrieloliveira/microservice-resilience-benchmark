package br.ufu.gabrieloliveira.ms_order_orchestrator.controller;

// Importe o novo serviço
import br.ufu.gabrieloliveira.ms_order_orchestrator.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Mude o retorno
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> createOrder() {

        // 1. Chamar Restaurante
        String respRestaurant = restTemplate.postForObject("http://ms-restaurant/validate", null, String.class);

        // 2. Chamar Pagamento (AGORA PROTEGIDO PELO CIRCUIT BREAKER)
        ResponseEntity<String> respPayment = paymentService.callPaymentService();

        // 3. Chamar Logística
        String respDelivery = restTemplate.postForObject("http://ms-delivery/deliver", null, String.class);

        // Se o pagamento falhou (fallback foi ativado), repasse o erro
        if (respPayment.getStatusCode().isError()) {
            return ResponseEntity.status(respPayment.getStatusCode()).body(respPayment.getBody());
        }

        // 4. Agregar todas as respostas (só se tudo deu certo)
        String responseBody = String.format(
                "{\"status\": \"PEDIDO_CRIADO\", \"restaurante\": %s, \"pagamento\": %s, \"logistica\": %s}",
                respRestaurant, respPayment.getBody(), respDelivery
        );

        return ResponseEntity.ok(responseBody);
    }
}