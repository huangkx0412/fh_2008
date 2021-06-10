package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ShopGateWayApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopGateWayApp.class, args);
    }
}
