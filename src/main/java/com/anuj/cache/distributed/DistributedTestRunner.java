package com.anuj.cache.distributed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedTestRunner {
    @Bean
    public CommandLineRunner testRouting(DistributedCacheRouter router) {
        return args -> {
            System.out.println("Routing test :");
            System.out.println("user1 routed to: " + router.route("user1"));
            System.out.println("user2 routed to: " + router.route("user2"));
            System.out.println("user3 routed to: " + router.route("user3"));
        };
    }
}