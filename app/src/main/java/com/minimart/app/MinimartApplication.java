package com.minimart.app;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.minimart")
@MapperScan(basePackages = "com.minimart", annotationClass = Mapper.class)
public class MinimartApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinimartApplication.class, args);
	}

}
