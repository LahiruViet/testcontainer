package com.example.testcontainer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(ApplicationConfiguration.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
