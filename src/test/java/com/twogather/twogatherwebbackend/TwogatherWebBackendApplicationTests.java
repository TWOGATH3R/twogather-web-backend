package com.twogather.twogatherwebbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.TimeZone;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=10")
class TwogatherWebBackendApplicationTests {

	@PostConstruct
	public void started() {
		// timezone 세팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	@Test
	void contextLoads() {
		System.out.println(LocalTime.now());
	}


}
