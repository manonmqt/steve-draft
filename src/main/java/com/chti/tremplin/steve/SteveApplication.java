package com.chti.tremplin.steve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class SteveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteveApplication.class, args);
	}

}
