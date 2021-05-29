package com.avakio.mailer;

import com.avakio.mailer.properties.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        BrokerProperties.class,
        AppSecurityProperties.class,
        CaffeineProperties.class,
        EventsTaskExecutorProperties.class,
        EmailProperties.class
})
public class MailerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailerApplication.class, args);
    }
}
