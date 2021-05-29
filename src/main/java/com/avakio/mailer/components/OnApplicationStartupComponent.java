package com.avakio.mailer.components;

import com.avakio.mailer.configurations.ErrorEmailQueueConfiguration;
import com.avakio.mailer.dto.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OnApplicationStartupComponent {

    @Autowired
    public OnApplicationStartupComponent(ErrorEmailQueueConfiguration errorEmailQueue,JmsEmailErrorProducerComponent jmsEmailErrorProducerComponent) {
        int n_CONSUMERS = Runtime.getRuntime().availableProcessors();
        for (int j = 0; j < n_CONSUMERS; j++) {
            new Thread(new ErrorEmailQueueHandlingComponent(errorEmailQueue, jmsEmailErrorProducerComponent)).start();
        }

        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }
}
