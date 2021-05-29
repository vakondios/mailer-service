package com.avakio.mailer.filters;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.dto.RequestHelperInfo;
import com.avakio.mailer.enums.AuditEventSourceEnum;
import com.avakio.mailer.enums.UserEventResultEnum;
import com.avakio.mailer.events.AppEventPublisher;
import com.avakio.mailer.events.AuditEvent;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Executed before the Basic Authentication
 * Be careful, not to be component,because it will run twice.
 */
@Slf4j
public class RequestFilter extends OncePerRequestFilter {
    private final AuditCache auditCache;
    private final AppEventPublisher appEventPublisher;

    public RequestFilter(AuditCache auditCache, AppEventPublisher appEventPublisher) {
        this.auditCache = auditCache;
        this.appEventPublisher = appEventPublisher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        AuditInfoDto auditRequest;
        String trxIdRequest = httpServletRequest.getHeader("x-server-transaction-id");
        if (CommonLib.isBlankString(trxIdRequest)) {
            trxIdRequest = auditCache.newTransaction("");
            if (log.isWarnEnabled()) log.warn("New incoming message without transaction id. A new transaction id has assigned with value {}", trxIdRequest);
        } else {
            trxIdRequest = auditCache.newTransaction(trxIdRequest);
        }

        httpServletResponse.addHeader("x-server-transaction-id", trxIdRequest);
        auditRequest = new AuditInfoDto(trxIdRequest, httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        httpServletRequest.setAttribute("x-server-transaction-id", auditRequest.getTransaction_id());
        RequestHelperInfo requestHelperInfo = new RequestHelperInfo(httpServletRequest.getRequestURI(), httpServletRequest.getMethod(), trxIdRequest);
        httpServletRequest.setAttribute("requestHelper", requestHelperInfo);

        if (log.isInfoEnabled())  log.info("[{}] => New incoming request for : [{} - {}].", auditRequest.getTransaction_id(), auditRequest.getTransaction_method(), auditRequest.getTransaction_URI());
        auditRequest.setUserKey(httpServletRequest.getHeader("x-client-user-id"));
        auditRequest.setSource(AuditEventSourceEnum.fromRequestController.getAuditEventSourceEnum());
        auditRequest.setRequestInfo(CommonLib.logRequestHeadersToMap(httpServletRequest));
        auditCache.newAudit(auditRequest);
        if (log.isInfoEnabled()) log.info("[{}] => Transaction executed successfully the Authentication/Authorization : [{} - {}]",
                auditRequest.getTransaction_id(), auditRequest.getTransaction_method(), auditRequest.getTransaction_URI());

        filterChain.doFilter(httpServletRequest, httpServletResponse);

        AuditInfoDto auditResponse;
        String trxIdResponse = (String) httpServletRequest.getAttribute("x-server-transaction-id");
        if (CommonLib.isBlankString(trxIdResponse)) {
            log.error("Http Response without a transaction id");
        } else {
            auditResponse = auditCache.getAudit(trxIdResponse);
            if (auditResponse == null) {
                log.error("Audit Cache expired before the system to send a response.");
                return;
            }
            //Check if it was success or not
            UserEventResultEnum userEventResultEnum;
            if ((httpServletResponse.getStatus() == 201) || (httpServletResponse.getStatus() == 200) || (httpServletResponse.getStatus() == 204)) {
                userEventResultEnum = UserEventResultEnum.executed;
            } else if (httpServletResponse.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                userEventResultEnum = UserEventResultEnum.notAuthorised;
            } else {
                userEventResultEnum = UserEventResultEnum.error;
            }

            if (log.isInfoEnabled())
                log.info("[{}] => New outgoing response for : [{} - {}] with status {}.", auditResponse.getTransaction_id(), auditResponse.getTransaction_method(), auditResponse.getTransaction_URI(), userEventResultEnum.name());

            auditResponse.setStatus(userEventResultEnum.getUserEventResultEnum());
            auditResponse.setResponseInfo(CommonLib.logResponseHeadersToMap(httpServletResponse));
            auditResponse.setRespondTimeStamp(System.currentTimeMillis());
            auditCache.updateAudit(auditResponse);
            appEventPublisher.OnAuditing(new AuditEvent(auditResponse.getTransaction_id()));
        }
    }
}
