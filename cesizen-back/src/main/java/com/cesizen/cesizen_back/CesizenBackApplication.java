package com.cesizen.cesizen_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CesizenBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CesizenBackApplication.class, args);
	}
}