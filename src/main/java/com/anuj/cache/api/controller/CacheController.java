package com.anuj.cache.api.controller;

import com.anuj.cache.distributed.DistributedCacheRouter;
import com.anuj.cache.api.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final DistributedCacheRouter router;

    public CacheController(DistributedCacheRouter router) {
        this.router = router;
    }

    // ✅ PUT with optional TTL
    @PostMapping
    public ResponseEntity<Map<String, Object>> put(
            @RequestParam String key,
            @RequestParam String value) {

                String result = router.put(key, value);
                return ResponseEntity.ok(Map.of(
                "status", result,
                "key", key,
                "value", value
        ));// clean HTTP response
    }
    // ✅ GET value
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String key) {

        String result = router.get(key);
        if (result == null || result.equals("NULL")) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", "NOT_FOUND",
                    "key", key
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "key", key,
                "value", result
        ));// clean HTTP response
    }
    // ✅ DELETE
    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String key) {

        String result = router.delete(key);
        return ResponseEntity.ok(Map.of(
                "status", result,
                "key", key
        ));
    }
       // ✅ STATS (clean output)
    @GetMapping("/cluster")
    public ResponseEntity<Map<String, Object>> cluster() {
        return ResponseEntity.ok(Map.of(
                "status", "ACTIVE",
                "nodes", List.of(
                        "localhost:9001",
                        "localhost:9002",
                        "localhost:9003"
                )
        ));
    }
}