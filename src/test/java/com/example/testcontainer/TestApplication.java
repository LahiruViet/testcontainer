package com.example.testcontainer;

import org.springframework.boot.SpringApplication;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(ApplicationConfiguration.class).run(args);
	}
}
