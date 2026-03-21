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
    @PostMapping
    public ResponseEntity<String> put(@RequestParam String key, @RequestParam String value) {
        cacheService.put(key, value);
        return ResponseEntity.ok("Stored successfully");
    }
    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        String value = cacheService.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }
    @DeleteMapping("/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {
        cacheService.delete(key);
        return ResponseEntity.ok("Deleted successfully");
    }
}