package com.avakio.mailer.enums;

import java.util.stream.Stream;

/**
 * Enumeration with the audit event source
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
public enum AuditEventSourceEnum {
    fromRequestController(1),
    fromResponseController(2),
    fromScheduler(3),
    fromFile(4),
    fromQueue(5),
    fromTopic(6);

    private final int auditEventSourceEnum;

    AuditEventSourceEnum(int auditEventSourceEnum) {
        this.auditEventSourceEnum = auditEventSourceEnum;
    }

    public int getAuditEventSourceEnum() {
        return auditEventSourceEnum;
    }

    public static AuditEventSourceEnum of(int auditEventSourceEnum) {
        return Stream.of(AuditEventSourceEnum.values())
                .filter(p -> p.getAuditEventSourceEnum() == auditEventSourceEnum)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
