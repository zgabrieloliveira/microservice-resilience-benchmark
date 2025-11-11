package br.ufu.gabrieloliveira.ms_restaurant.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController {

    @PostMapping("/validate")
    public String validateOrder() {
        return "{\"status\": \"RESTAURANT_OK\"}";
    }
}
