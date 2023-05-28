package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(QueryDslConfig.class)
public class RepositoryTest {

}