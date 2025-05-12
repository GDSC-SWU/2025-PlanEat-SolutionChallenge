package com.gdgswu.planeat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PlaneatApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaneatApplication.class, args);
	}

}
