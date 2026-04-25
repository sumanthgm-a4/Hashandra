package com.sum.hashandra_node1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HashandraNode1Application {

	public static void main(String[] args) {
		SpringApplication.run(HashandraNode1Application.class, args);
	}

}
