package com.avakio.mailer.advices;

import com.avakio.mailer.exceptions.AppCannotReadTheBodyException;
import org.apache.commons.io.IOUtils;
import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Auditing the request incoming body
 */
@ControllerAdvice
@Slf4j
public class RequestAuditBodyAdvice implements RequestBodyAdvice {

    private final AuditCache auditCache;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public RequestAuditBodyAdvice(HttpServletRequest httpServletRequest,
                                  AuditCache auditCache) {
        this.httpServletRequest = httpServletRequest;
        this.auditCache = auditCache;
        if (log.isDebugEnabled()) log.debug("Component Initialized");
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage,
                                           MethodParameter methodParameter,
                                           Type type,
                                           Class<? extends HttpMessageConverter<?>> aClass) throws IOException {

        AuditInfoDto audit = auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));
        CustomHttpInputMessage customHttpInputMessage = new CustomHttpInputMessage(httpInputMessage, audit);
        if (log.isInfoEnabled())
            log.info("[{}] => RequestBodyAdvice for auditing : [{} - {}].", audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI());

        InputStreamReader inputStreamReader = new InputStreamReader(customHttpInputMessage.getBody(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String jsonString = CommonLib.bufferReaderToString(bufferedReader);
        Map<String, Object> requestMap = CommonLib.jsonStringToMap(jsonString);
        audit.setRequestBody(requestMap);

        return customHttpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (log.isDebugEnabled()) {
            AuditInfoDto audit = auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));
            log.debug("[{}] => RequestBodyAdvice for auditing : [{} - {}].", audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI());
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (log.isDebugEnabled()) {
            AuditInfoDto audit = auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));
            log.debug("[{}] => RequestBodyAdvice for auditing : [{} - {}].", audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI());
        }
        return body;
    }

    private class CustomHttpInputMessage implements HttpInputMessage {
        private final HttpInputMessage origin;
        private final AuditInfoDto auditInfoDto;
        private String classBody;

        public CustomHttpInputMessage(HttpInputMessage httpInputMessage, AuditInfoDto auditInfoDto) {
            this.origin = httpInputMessage;
            this.auditInfoDto = auditInfoDto;

            try {
                this.classBody = IOUtils.toString(origin.getBody(), Charset.defaultCharset());
            } catch (Exception ex) {
                this.classBody = "";
            }
        }

        @Override
        public HttpHeaders getHeaders() {
            return origin.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (classBody.equals("")) {
                throw new AppCannotReadTheBodyException(origin, auditInfoDto, "CustomHttpInputMessage", "getBody()");
            } else {
                return IOUtils.toInputStream(classBody, "UTF-8");
            }
        }
    }
}