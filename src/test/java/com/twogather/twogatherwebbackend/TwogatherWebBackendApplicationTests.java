package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=10")
class TwogatherWebBackendApplicationTests {

	@Test
	void contextLoads() {
	}


}
