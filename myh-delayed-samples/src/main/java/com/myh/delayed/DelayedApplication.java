package com.myh.delayed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实现延时业务场景;
 */
@SpringBootApplication
public class DelayedApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelayedApplication.class, args);
	}

}
