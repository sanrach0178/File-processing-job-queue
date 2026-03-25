package com.example.jobqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobQueueSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobQueueSystemApplication.class, args);
	}

}
