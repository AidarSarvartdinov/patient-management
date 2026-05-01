package com.pm.scheduling_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SchedulingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingServiceApplication.class, args);
	}

}
