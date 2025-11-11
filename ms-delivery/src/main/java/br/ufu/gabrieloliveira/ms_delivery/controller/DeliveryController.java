package br.ufu.gabrieloliveira.ms_delivery.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryController {
    @PostMapping("/deliver")
    public String deliver() {
        return "{\"status\": \"DELIVERY_OK\"}";
    }
}