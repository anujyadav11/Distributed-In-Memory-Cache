package com.anuj.cache.distributed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DistributedConfig {
    @Bean
    public ConsistentHashing consistentHashing() {
        List<String> nodes = List.of(
                "localhost:9001",
                "localhost:9002",
                "localhost:9003"
);
        return new ConsistentHashing(nodes, 100);
    }
    // ✅ ADD THIS (missing bean)
    @Bean
    public TcpCacheClient tcpCacheClient() {
        return new TcpCacheClient();
    }
    @Bean
    public NodeHealthTracker nodeHealthTracker() {
        return new NodeHealthTracker();
    }
    // ✅ FIXED constructor injection
    @Bean
    public DistributedCacheRouter distributedCacheRouter(
            ConsistentHashing consistentHashing,
            TcpCacheClient tcpCacheClient,
            NodeHealthTracker healthTracker) {

        return new DistributedCacheRouter(consistentHashing, tcpCacheClient, healthTracker);
    }
}