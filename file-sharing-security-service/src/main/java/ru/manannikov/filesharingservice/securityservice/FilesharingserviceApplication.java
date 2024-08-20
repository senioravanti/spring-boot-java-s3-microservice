package ru.manannikov.filesharingservice.securityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import ru.manannikov.filesharingservice.securityservice.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableDiscoveryClient
public class FilesharingserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilesharingserviceApplication.class, args);
	}

}
