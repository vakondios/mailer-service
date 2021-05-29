package com.avakio.mailer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.WebRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Servlet functions for the project
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
public class ServletLib {

    /**
     * Copy all the request headers to response headers
     * (if they do not exits)
     *
     * @param httpServletRequest  HttpServletRequest
     * @param httpServletResponse HttpServletResponse
     */
    public static void copyRequestHeadersToResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (httpServletResponse.getHeader(headerName) == null) {
                httpServletResponse.addHeader(headerName, httpServletRequest.getHeader(headerName));
            }
        }
    }


    /**
     * Creates HttpHeaders from WebRequest
     *
     * @param request WebRequest
     * @return HttpHeaders
     */
    public static HttpHeaders getHeaders(WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Iterator<String> headerNames = request.getHeaderNames();
        while (headerNames.hasNext()) {
            String headerName = headerNames.next();
            httpHeaders.add(headerName, request.getHeader(headerName));
        }
        return httpHeaders;
    }
}
