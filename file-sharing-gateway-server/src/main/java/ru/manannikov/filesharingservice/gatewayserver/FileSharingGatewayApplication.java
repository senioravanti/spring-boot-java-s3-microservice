package ru.manannikov.filesharingservice.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
public class FileSharingGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSharingGatewayApplication.class, args);
	}
}
