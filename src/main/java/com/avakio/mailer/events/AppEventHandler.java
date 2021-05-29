package com.avakio.mailer.events;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Application Events
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
@Service
public class AppEventHandler {

    private final AuditCache auditCache;

    @Autowired
    public AppEventHandler(AuditCache auditCache) {
        if (log.isDebugEnabled()) log.debug("Service Initialized.");
        this.auditCache = auditCache;
    }


    @EventListener
    @Async("AuditEventTaskExecutor")
    public void handleEvent(AuditEvent auditEvent) {
        AuditInfoDto info = auditCache.getAudit(auditEvent.getTransaction_id());
        closureRequest(info);

        //Delete from cache.
        auditCache.deleteAudit(info);
        //TODO:Send to ELK Microservice
        //log.error(CommonLib.ObjectToJsonString(info));
    }

    private void closureRequest(AuditInfoDto info) {
        if (log.isInfoEnabled())
            log.info("[{}] => Transaction total time for [{} - {}] : {} ms", info.getTransaction_id(), info.getTransaction_method(), info.getTransaction_URI(), info.getRespondTimeStamp() - info.getRequestTimeStamp());

    }
}