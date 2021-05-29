package com.avakio.mailer.configurations;

import com.avakio.mailer.dto.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class ErrorEmailQueueConfiguration {

    private final transient BlockingQueue<ErrorInfo> errorEmailQueue;

    public ErrorEmailQueueConfiguration() {
        this.errorEmailQueue = new  LinkedBlockingQueue<>();
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }

    public BlockingQueue<ErrorInfo> getQueue(){
        return errorEmailQueue;
    }
}
