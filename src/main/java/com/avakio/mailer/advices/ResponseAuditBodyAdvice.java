package com.avakio.mailer.advices;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.utils.CommonLib;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Auditing the response outgoing body
 */
@ControllerAdvice
@Slf4j
public class ResponseAuditBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final AuditCache auditCache;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ResponseAuditBodyAdvice(ObjectMapper objectMapper, HttpServletRequest httpServletRequest, AuditCache auditCache){
        if (log.isDebugEnabled()) log.debug("Component Initialized");
        this.objectMapper = objectMapper;
        this.httpServletRequest = httpServletRequest;
        this.auditCache = auditCache;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        AuditInfoDto audit = CommonLib.returnAuditInfo(httpServletRequest,auditCache);//auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));
        if (log.isInfoEnabled())
            log.info("[{}] => ResponseBodyAdvice for transaction : [{} - {}].", audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI());

        try {
            audit.setResponseBody(objectMapper.convertValue(body, Map.class));
        } catch (Exception ex) {
            log.error("[{}] => System cannot convert the value for transaction : [{} - {}].", audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI());
            log.error("[{}] => Response Body :{}",audit.getTransaction_id(), body);
            audit.setResponseBody(body);
        }
        finally {
            auditCache.updateAudit(audit);
        }

        return body;
    }
}
