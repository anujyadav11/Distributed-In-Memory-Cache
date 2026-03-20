package com.anuj.cache.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.anuj.cache"}
)
public class CacheApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheApiApplication.class, args);
    }
}