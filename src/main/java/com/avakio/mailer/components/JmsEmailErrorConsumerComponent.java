package com.avakio.mailer.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsEmailErrorConsumerComponent {

    @JmsListener(destination = "email_error_topic", containerFactory = "jmsTopicListenerContainerFactory")
    public void processTopicMessage(String content) {
        log.error(content);
    }

}
