package com.avakio.mailer.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Created Event for the auditing
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Getter
public class AuditEvent extends ApplicationEvent {

    private final String transaction_id;

    public AuditEvent(String transaction_id) {
        super(transaction_id);
        this.transaction_id = transaction_id;
    }
}