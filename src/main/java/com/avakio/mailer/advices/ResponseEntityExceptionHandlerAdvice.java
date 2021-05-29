package com.avakio.mailer.advices;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.dto.ResponseInfo;
import com.avakio.mailer.properties.AppProperties;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;


/**
 * Global Exception Handler for Controllers
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
@RestControllerAdvice
public class ResponseEntityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private final AppProperties appProperties;
    private final AuditCache auditCache;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ResponseEntityExceptionHandlerAdvice(AppProperties appProperties,AuditCache auditCache, HttpServletRequest httpServletRequest) {
        super();
        if (log.isDebugEnabled()) log.debug("RestControllerAdvice Initialized.");
        this.appProperties = appProperties;
        this.auditCache = auditCache;
        this.httpServletRequest = httpServletRequest;
    }



    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleBindException(org.springframework.validation.BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        pageNotFoundLogger.warn(ex.getMessage());
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }
        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
            if (request instanceof ServletWebRequest) {
                ServletWebRequest servletWebRequest = (ServletWebRequest)request;
                if (HttpMethod.PATCH.equals(servletWebRequest.getHttpMethod())) {
                    headers.setAcceptPatch(mediaTypes);
                }
            }
        }

        return buildErrorResponseForExceptions(ex, status, request,  headers);
    }

    @Override
    public ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest)webRequest;
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                AuditInfoDto audit = CommonLib.returnAuditInfo(httpServletRequest, auditCache);

                if (log.isWarnEnabled()) log.warn("[{}] => Async request timed out : [{} - {}], with message: {}",
                        audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI(), ex.getMessage());

                return null;
            }
        }

        return buildErrorResponse(ex, ex.getMessage(), status, webRequest, headers);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }

        return buildErrorResponse(ex,  ex.getMessage(), status, request, headers);

    }

    // 400
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request) {
        log.error("Data Integrity Violation", exception);
        return buildErrorResponse(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, request, null); //servletLib.getHeaders(request)
    }

    //500
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return buildErrorResponse(exception, "Unknown_error_occurred", HttpStatus.INTERNAL_SERVER_ERROR, request, null); //servletLib.getHeaders(request)
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus, WebRequest request, HttpHeaders headers) {
        ResponseInfo errorResponseInfo = new ResponseInfo(appProperties.getName(), appProperties.getVersion());
        HttpHeaders responseHeaders = headers == null ? new HttpHeaders() : headers;
        AuditInfoDto audit = auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));

        if (log.isWarnEnabled()) log.warn("[{}] => Error on transaction : [{} - {}], with message: {}",
                audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI(), message);

        errorResponseInfo.setErrors(CommonLib.getStackTraceErrors("com.avakio", exception.getStackTrace()));
        errorResponseInfo.setStatus(httpStatus.value());
        errorResponseInfo.setError(exception.toString());
        errorResponseInfo.setMessage(message);
        errorResponseInfo.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(errorResponseInfo, responseHeaders, HttpStatus.valueOf(errorResponseInfo.getStatus()));
    }

    private ResponseEntity<Object> buildErrorResponseForExceptions(Exception exception, HttpStatus httpStatus, WebRequest request, HttpHeaders headers) {
        ResponseInfo errorResponseInfo = new ResponseInfo(appProperties.getName(), appProperties.getVersion());
        HttpHeaders responseHeaders = headers == null ? new HttpHeaders() : headers;
        AuditInfoDto audit = auditCache.getAudit((String) httpServletRequest.getAttribute("x-server-transaction-id"));

        if (log.isWarnEnabled()) log.warn("[{}] => Error on transaction : [{} - {}], with message: {}",
                audit.getTransaction_id(), audit.getTransaction_method(), audit.getTransaction_URI(), exception.getMessage());

        errorResponseInfo.setStatus(httpStatus.value());
        errorResponseInfo.setError(exception.getMessage());
        errorResponseInfo.setMessage(CommonLib.getClassName(exception.getClass().getCanonicalName()));
        errorResponseInfo.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(errorResponseInfo, responseHeaders, HttpStatus.valueOf(errorResponseInfo.getStatus()));
    }

}
