package com.avakio.mailer.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsEmailErrorProducerComponent {
    @Autowired
    private JmsTemplate jmsTopicTemplate; // Uses the implementation with the topic

    public void sendEmail(String message) {
        String queueName ="email_error_topic";
        try{
            if (log.isInfoEnabled()) log.info("Attempting Send message to Topic: "+ queueName);
            jmsTopicTemplate.convertAndSend(queueName, message);
        } catch(Exception e){
            log.error("Received Exception during send Message: ", e);
        }
    }
}
