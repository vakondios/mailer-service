package com.avakio.mailer.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
@Component
@Slf4j
@Data
public class AppSecurityProperties {
    private String username = "systemMailer";
    private String password = "jasdja@@12dfknlkdfk32klkldksdfkldf";
    private List<String> uriAuthWhitelist;

    public AppSecurityProperties() {
        if (log.isDebugEnabled())  log.debug("Component initialized");
    }
}
