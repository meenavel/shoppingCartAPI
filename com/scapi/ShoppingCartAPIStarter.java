package com.scapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories
@EnableScheduling
public class ShoppingCartAPIStarter {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartAPIStarter.class, args);
	}

	@Bean
	public RestTemplate loadRestTemplate(){
		return new RestTemplate();
	}

}
