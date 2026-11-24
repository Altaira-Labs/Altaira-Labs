package com.altaira.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Spring application and scans the backend packages for controllers, services and persistence adapters.
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Bootstraps the Spring application using the supplied command-line arguments.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
