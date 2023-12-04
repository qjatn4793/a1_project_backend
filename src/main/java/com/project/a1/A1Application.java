package com.project.a1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value={"com.project.a1.**.mapper"})
public class A1Application {

	public static void main(String[] args) {
		SpringApplication.run(A1Application.class, args);
	}

}
