package br.ufu.gabrieloliveira.ms_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("orquestrador-route", r -> r
                        // 1. O Predicado (O "se"):
                        // (O que estava no seu predicates[0])
                        .path("/api/v1/orders/**")

                        // 2. O Filtro (O "faça isso"):
                        // (O que estava no seu filters[0], mas usando StripPrefix que é mais limpo)
                        .filters(f -> f.stripPrefix(2)) // Remove /api/v1/

                        // 3. O URI (O "envie para cá"):
                        // (O que estava no seu uri)
                        .uri("lb://ms-order-orchestrator"))

                // Se precisar adicionar outras rotas, adicione outro .route(...) aqui

                .build();
    }
}
