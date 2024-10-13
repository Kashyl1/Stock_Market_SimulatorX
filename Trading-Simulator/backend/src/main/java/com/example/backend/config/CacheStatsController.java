package com.example.backend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheStatsController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/cache-stats")
    public String getCacheStats() {
        CaffeineCache currentUserCache = (CaffeineCache) cacheManager.getCache("currentUser");
        if (currentUserCache == null) {
            return "Cache 'currentUser' not found";
        }
        Cache<Object, Object> nativeCache = currentUserCache.getNativeCache();
        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) nativeCache;
        CacheStats stats = caffeine.stats();
        return "Cache Hits: " + stats.hitCount() +
                ", Cache Misses: " + stats.missCount() +
                ", Hit Rate: " + stats.hitRate();
    }
}
