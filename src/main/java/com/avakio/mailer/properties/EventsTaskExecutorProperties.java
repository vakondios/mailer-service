package com.avakio.mailer.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "events-task-executor")
@Component
@Slf4j
public class EventsTaskExecutorProperties {
    private int CorePoolSize = 2;
    private int MaxPoolSize = 5;
    private int QueueCapacity = 250;

    public EventsTaskExecutorProperties() {
        if (log.isDebugEnabled()) log.debug("Component initialized");
    }
}
