package com.atguigu.yygh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceCmnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCmnApplication.class, args);
	}

}
