package ru.manannikov.filesharingservice.s3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
//@RefreshScope
//@EnableDiscoveryClient
public class FileSharingS3Application {

	public static void main(String[] args) {
		SpringApplication.run(FileSharingS3Application.class, args);
	}

}
