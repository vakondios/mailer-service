package com.avakio.mailer.components;

import com.avakio.mailer.dto.EmailMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsEmailProducerComponent {

    @Autowired
    private  JmsTemplate jmsQueueTemplate;  // Uses the implementation with the Queue

    public void sendEmail(EmailMessageDto emailMessageDto) {
        String queueName ="email_queue";
        try{
            if (log.isInfoEnabled())  log.info("[{}] => Attempting to send message to Queue: {}.",
                    emailMessageDto.getTrxId(),  queueName);

            jmsQueueTemplate.convertAndSend(queueName, emailMessageDto.toJson());

            if (log.isInfoEnabled())  log.info("[{}] => Message sent to Queue: {}.",
                    emailMessageDto.getTrxId(),  queueName);
        } catch(Exception e){
            log.error("[{}] => Received Exception during send Message : {}.",
                    emailMessageDto.getTrxId(),  e.getMessage());
        }
    }




}
