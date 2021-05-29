package com.avakio.mailer.components;

import com.avakio.mailer.dto.EmailMessageDto;
import com.avakio.mailer.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsEmailConsumerComponent {

    private final EmailService emailService;
    private final String destination = "email_queue";
    private final String containerFactory = "jmsQueueListenerContainerFactory";

    @Autowired
    public JmsEmailConsumerComponent(EmailService emailService) {
        this.emailService = emailService;

        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }


    @JmsListener(destination = destination, containerFactory = containerFactory)
    public void processTopicMessage(String content) {
        if (log.isInfoEnabled())  log.info("Attempting to receive message to Queue: {}.",  destination);
        EmailMessageDto email = EmailMessageDto.getInstance(EmailMessageDto.class,content);

        if (email !=null) {
            if (log.isInfoEnabled())  log.info("[{}] => Received a mailer message to Queue: {}.",
                    email.getTrxId(),  destination);
        } else {
            if (log.isWarnEnabled())  log.warn("Received a mailer message to Queue: {} with error.", destination);
        }

        emailService.sendEmail(email);
    }
}
