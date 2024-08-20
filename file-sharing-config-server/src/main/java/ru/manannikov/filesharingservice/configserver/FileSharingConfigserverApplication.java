package ru.manannikov.filesharingservice.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigServer
@RestController
public class FileSharingConfigserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSharingConfigserverApplication.class, args);
	}

}
