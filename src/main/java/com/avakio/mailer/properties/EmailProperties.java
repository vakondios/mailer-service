package com.avakio.mailer.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "app.mail")
@Component
@Slf4j
public class EmailProperties {
    private String from;
    private String host;
    private String username;
    private String password;
    private String transportProtocol;
    private int smtpPort;
    private int connectionTimeOut;
    private boolean smtpAuth;
    private boolean starttlsEnable;
    private boolean starttlsRequired;

    public EmailProperties() {
        if (log.isDebugEnabled()) log.debug("Component Initialized");
    }

}
