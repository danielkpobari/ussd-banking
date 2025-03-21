package com.daniel.skaet_ussd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableCaching
@EnableWebSecurity
public class SkaetUssdApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkaetUssdApplication.class, args);
	}

}
