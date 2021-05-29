package com.avakio.mailer.configurations;

import com.avakio.mailer.properties.EventsTaskExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configure the properties of the Custom Task Executor
 * with the name "auditEventTaskExecutor"
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
@Configuration
@EnableAsync
public class AppAsyncConfiguration {

    private final EventsTaskExecutorProperties eventsTaskExecutorProperties;

    @Autowired
    public AppAsyncConfiguration(EventsTaskExecutorProperties eventsTaskExecutorProperties) {
        this.eventsTaskExecutorProperties = eventsTaskExecutorProperties;
        if (log.isDebugEnabled()) log.debug("Configuration initialized");
    }

    @Bean(name = "AuditEventTaskExecutor")
    public Executor auditEventTaskExecutor() {
        if (log.isInfoEnabled()) log.info("Configuration for auditEventTaskExecutor initializing with values [corePoolSize=" +
                eventsTaskExecutorProperties.getCorePoolSize() + "],[maxPoolSize=" + eventsTaskExecutorProperties.getMaxPoolSize() +
                "],[queueCapacity=" + eventsTaskExecutorProperties.getQueueCapacity() + "]");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(eventsTaskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(eventsTaskExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(eventsTaskExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix("auditEvent-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "EmailSendTaskExecutor")
    public Executor emailSendTaskExecutor() {
        if (log.isInfoEnabled()) log.info("Configuration for emailSendTaskExecutor initializing with values [corePoolSize=" +
                eventsTaskExecutorProperties.getCorePoolSize() + "],[maxPoolSize=" + eventsTaskExecutorProperties.getMaxPoolSize() +
                "],[queueCapacity=" + eventsTaskExecutorProperties.getQueueCapacity() + "]");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(eventsTaskExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(eventsTaskExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(eventsTaskExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix("emailSend-");
        executor.initialize();
        return executor;
    }
}