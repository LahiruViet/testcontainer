package com.example.testcontainer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ApplicationConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgres() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"));
	}

}
