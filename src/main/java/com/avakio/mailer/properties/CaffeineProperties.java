package com.avakio.mailer.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "app.caffeine.cache")
@Component
@Slf4j
public class CaffeineProperties {
    private int initialCapacity = 100;
    private int maximumSize = 500;
    private int expireInSeconds = 60;

    public CaffeineProperties() {
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }
}