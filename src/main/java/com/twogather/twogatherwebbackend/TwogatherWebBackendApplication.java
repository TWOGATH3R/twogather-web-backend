package com.twogather.twogatherwebbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class TwogatherWebBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwogatherWebBackendApplication.class, args);
	}

}
