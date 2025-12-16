package com.example.playground.cache.source;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();

        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                        .map(cache -> new CaffeineCache(
                                cache.getName(),
                                Caffeine.newBuilder()
                                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.HOURS)
                                        .maximumSize(cache.getMaximumSize())
                                        .recordStats()
                                        .build()
                        )).collect(Collectors.toList());

        manager.setCaches(caches);
        return manager;
    }

}
