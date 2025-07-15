package com.kubertech.rewardsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Reward Points Service application.
 * <p>
 * This class bootstraps the Spring Boot environment and launches the embedded server.
 * All Spring components such as services, repositories, and controllers are auto-configured from this base package.
 */
@SpringBootApplication
public class RewardPointsServiceApplication {

	/**
	 * Main method to launch the application.
	 *
	 * @param args command-line arguments passed at startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(RewardPointsServiceApplication.class, args);
	}
}