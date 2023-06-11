package com.twogather.twogatherwebbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=10")
class TwogatherWebBackendApplicationTests {

	@Test
	void contextLoads() {
	}


}
