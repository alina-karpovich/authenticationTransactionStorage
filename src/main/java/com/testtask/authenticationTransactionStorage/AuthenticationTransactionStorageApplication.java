package com.testtask.authenticationTransactionStorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.testtask.authenticationTransactionStorage.persistence")
@EntityScan("com.testtask.authenticationTransactionStorage.persistence.model")
public class AuthenticationTransactionStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationTransactionStorageApplication.class, args);
	}

}
