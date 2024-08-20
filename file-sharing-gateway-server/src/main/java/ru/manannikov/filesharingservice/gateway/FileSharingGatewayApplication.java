package ru.manannikov.filesharingservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RefreshScope
public class FileSharingGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSharingGatewayApplication.class, args);
	}
}
