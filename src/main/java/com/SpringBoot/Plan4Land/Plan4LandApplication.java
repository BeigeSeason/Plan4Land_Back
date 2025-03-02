package com.SpringBoot.Plan4Land;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Plan4LandApplication {

	public static void main(String[] args) {
		SpringApplication.run(Plan4LandApplication.class, args);
	}

}
