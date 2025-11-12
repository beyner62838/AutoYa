package com.example.back_AutoYa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BackAutoYaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackAutoYaApplication.class, args);
	}

}
