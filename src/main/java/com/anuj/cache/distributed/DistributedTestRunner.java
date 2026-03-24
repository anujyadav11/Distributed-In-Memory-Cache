package com.anuj.cache.distributed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedTestRunner {

    @Bean
    public CommandLineRunner testRouting(DistributedCacheRouter router,
                                        ConsistentHashing hashing) {
        return args -> {

            System.out.println("\n================ ROUTING TEST ================\n");

            // ✅ Check ring size
            System.out.println("Ring size: " + hashing.getRingSize());

            // ✅ Test distribution
            for (int i = 1; i <= 20; i++) {
                String key = "user" + i;
                String node = router.route(key);

                System.out.println(key + " → " + node);
            }

            System.out.println("\n==============================================\n");
        };
    }
}