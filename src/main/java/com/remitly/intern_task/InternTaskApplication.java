package com.remitly.intern_task;

import com.remitly.intern_task.service.SwiftCodeParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InternTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternTaskApplication.class, args);
	}

	@Bean
	CommandLineRunner run(SwiftCodeParser swiftCodeParser) {
		return args -> {
			swiftCodeParser.processSwiftCodes("src/main/resources/Interns_2025_SWIFT_CODES.csv");
		};
	}
}
