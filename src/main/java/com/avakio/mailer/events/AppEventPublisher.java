package com.avakio.mailer.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Application Events
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
@Component
public class AppEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AppEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Published an event for a new audit
     *
     * @param auditEvent AuditEvent
     */
    public void OnAuditing(AuditEvent auditEvent) {
        if (log.isInfoEnabled()) log.info("[{}] => Published a new audit event.", auditEvent.getTransaction_id());
        applicationEventPublisher.publishEvent(auditEvent);
    }
}