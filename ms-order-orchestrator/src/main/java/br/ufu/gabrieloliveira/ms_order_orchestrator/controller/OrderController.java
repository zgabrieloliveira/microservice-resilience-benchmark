package br.ufu.gabrieloliveira.ms_order_orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public String createOrder() {
        // 1. chama restaurante
         String respRestaurant = restTemplate.postForObject("http://ms-restaurant/validate", null, String.class);

        // 2. Chamar pagamento
        String respPayment = restTemplate.postForObject("http://ms-payment/pay", null, String.class);

        // 3. Chamar logística
         String respDelivery = restTemplate.postForObject("http://ms-delivery/deliver", null, String.class);

        // 4. Agregar todas as respostas
        return String.format(
            "{\"status\": \"PEDIDO_CRIADO\", \"restaurante\": %s, \"pagamento\": %s, \"logistica\": %s}",
            respRestaurant, respPayment, respDelivery
        );
    }
}
