package com.ecommerce.eurekamicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekamicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekamicroserviceApplication.class, args);
    }

}
