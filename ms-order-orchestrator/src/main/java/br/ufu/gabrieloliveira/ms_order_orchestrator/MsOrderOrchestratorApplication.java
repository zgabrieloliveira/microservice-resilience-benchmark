package br.ufu.gabrieloliveira.ms_order_orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsOrderOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsOrderOrchestratorApplication.class, args);
	}

}
