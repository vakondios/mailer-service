package com.avakio.mailer.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "broker")
@Component
@Slf4j
public class BrokerProperties {
    private boolean persistent = false;
    private boolean deleteAllMessagesOnStartup = false;
    private String network = "localhost";
    private String name = "Broker";
    private String username = "admin";
    private String password = "admin";
    private int timeToLive=0;
    private int port = 61616;

    public BrokerProperties() {
        if (log.isDebugEnabled()) log.debug("Component initialized");
    }
}
