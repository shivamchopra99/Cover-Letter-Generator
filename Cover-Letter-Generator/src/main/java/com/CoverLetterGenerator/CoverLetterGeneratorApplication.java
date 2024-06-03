package com.CoverLetterGenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CoverLetterGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoverLetterGeneratorApplication.class, args);
	}

}
