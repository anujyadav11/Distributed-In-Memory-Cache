package com.anuj.cache.api.controller;

import com.anuj.cache.api.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    // ✅ PUT with optional TTL
    @PostMapping
    public ResponseEntity<String> put(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) Long ttl) {

        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().body("Key cannot be empty");
        }

        if (ttl != null) {
            cacheService.putWithTTL(key, value, ttl);
        } else {
            cacheService.put(key, value);
        }

        return ResponseEntity.status(201).body("Stored successfully");
    }

    // ✅ GET value
    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {

        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String value = cacheService.get(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value); // clean HTTP response
    }

    // ✅ STATS (clean output)
    @GetMapping("/stats")
    public ResponseEntity<String> stats() {
        return ResponseEntity.ok(cacheService.getStats());
    }

    // ✅ DELETE
    @DeleteMapping("/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {

        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        cacheService.delete(key);
        return ResponseEntity.ok("Deleted successfully");
    }
}