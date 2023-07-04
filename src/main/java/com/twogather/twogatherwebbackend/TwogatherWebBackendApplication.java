package com.twogather.twogatherwebbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class TwogatherWebBackendApplication {

	@PostConstruct
	public void started() {
		// timezone 세팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(TwogatherWebBackendApplication.class, args);
	}

}
