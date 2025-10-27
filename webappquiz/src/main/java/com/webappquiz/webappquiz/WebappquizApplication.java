package com.webappquiz.webappquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WebappquizApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebappquizApplication.class, args);
	}

}
