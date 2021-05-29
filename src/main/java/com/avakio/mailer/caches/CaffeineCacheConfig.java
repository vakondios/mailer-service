package com.avakio.mailer.caches;

import com.avakio.mailer.properties.CaffeineProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CaffeineCacheConfig  {

    private final CaffeineProperties caffeineProperties;

    @Autowired
    public CaffeineCacheConfig(CaffeineProperties caffeineProperties){
        this.caffeineProperties = caffeineProperties;
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.getCache("audit");
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(caffeineProperties.getInitialCapacity())
                .maximumSize(caffeineProperties.getMaximumSize())
                .expireAfterWrite(caffeineProperties.getExpireInSeconds(), TimeUnit.SECONDS)
                .weakKeys();
    }
}