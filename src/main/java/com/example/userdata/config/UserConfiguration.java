package com.example.userdata.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
@EnableCaching
public class UserConfiguration {

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS);
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
