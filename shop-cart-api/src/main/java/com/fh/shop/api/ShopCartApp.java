package com.fh.shop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShopCartApp {

    public static void main(String[] args) {
        System.out.println("这是shopcartapp的启动方法");
        SpringApplication.run(ShopCartApp.class, args);
    }


}
