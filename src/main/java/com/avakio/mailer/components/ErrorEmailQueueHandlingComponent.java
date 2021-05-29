package com.avakio.mailer.components;

import com.avakio.mailer.configurations.ErrorEmailQueueConfiguration;
import com.avakio.mailer.dto.ErrorInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads the Error queue and log the error.
 */
@Slf4j
public class ErrorEmailQueueHandlingComponent implements Runnable{

    private final ErrorEmailQueueConfiguration errorEmailQueue;
    private final JmsEmailErrorProducerComponent jmsEmailErrorProducerComponent;

    public ErrorEmailQueueHandlingComponent(ErrorEmailQueueConfiguration errorEmailQueue, JmsEmailErrorProducerComponent jmsEmailErrorProducerComponent)
    {
        this.errorEmailQueue = errorEmailQueue;
        this.jmsEmailErrorProducerComponent = jmsEmailErrorProducerComponent;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ErrorInfo err = errorEmailQueue.getQueue().take();
                if (err !=null) {
                    log.error("[{}] => Email did not send. Reason: {}", err.getTrxId(), err.getMessage());
                    jmsEmailErrorProducerComponent.sendEmail("[" + err.getTrxId() + "] => Email did not send. Reason: " + err.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
