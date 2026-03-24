package com.anuj.cache.api.controller;

import com.anuj.cache.distributed.DistributedCacheRouter;
import com.anuj.cache.api.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final DistributedCacheRouter router;

    public CacheController(DistributedCacheRouter router) {
        this.router = router;
    }

    // ✅ PUT with optional TTL
    @PostMapping
    public ResponseEntity<String> put(
            @RequestParam String key,
            @RequestParam String value) {

                if(key == null || key.isBlank()){
                    return ResponseEntity.badRequest().body("Key cannot be empty");
                }
                String response = router.put(key, value);
                return ResponseEntity.ok(response);
    }

    // ✅ GET value
    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {

        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String response = router.get(key);

        if (response == null || response.equals("NULL")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response); // clean HTTP response
    }
    // ✅ DELETE
    @DeleteMapping("/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {

        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String response = router.delete(key);
        return ResponseEntity.ok(response);
    }
       // ✅ STATS (clean output)
    @GetMapping("/stats")
    public ResponseEntity<String> stats() {
        return ResponseEntity.ok("Stats endpoint not implemented yet");
    }
}