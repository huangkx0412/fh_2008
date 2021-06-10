package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ShopRegApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopRegApp.class, args);
    }
}
