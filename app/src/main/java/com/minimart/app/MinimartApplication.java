package com.minimart.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.minimart")
public class MinimartApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinimartApplication.class, args);
	}

}
