package com.yotouch.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.yotouch"})
public class PylonApplication {

	public static void main(String[] args) {
		SpringApplication.run(PylonApplication.class, args);
	}
}
