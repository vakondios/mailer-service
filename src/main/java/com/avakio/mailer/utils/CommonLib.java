package com.avakio.mailer.utils;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.dto.ValidationFieldInfo;
import com.avakio.mailer.dto.ErrorInfo;
import com.avakio.mailer.exceptions.validation.FieldFormatValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Common functions for the project
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
public class CommonLib {
    /**
     * it used from properties for validation
     *
     * @param value         String
     * @param defaultValues List<String>
     * @return boolean
     */
    public static boolean isNotBlankAndSpecificValues(String value, List<String> defaultValues) {
        if (StringUtils.isNotBlank(value)) {
            return defaultValues.contains(value);
        } else {
            return false;
        }
    }

    /**
     * it used from properties for validation
     *
     * @param value  String
     * @param length int
     * @return boolean
     */
    public static boolean isNotBlankAndSpecificLength(String value, int length) {
        if (StringUtils.isNotBlank(value)) {
            return value.trim().length() == length;
        } else {
            return false;
        }
    }

    public static boolean isNotBlankString(String value) {
        return StringUtils.isNotBlank(value);
    }

    public static boolean isBlankString(String value){
        return !StringUtils.isNotBlank(value);
    }

    public static boolean isMandatoryValidString(String value, int lenString) {
        if (StringUtils.isNotBlank(value)) {
            return value.trim().length() < lenString;
        } else {
            return false;
        }
    }

    /**
     * Find out from Stacktrace only the rows that it refers to application according to Classname
     *
     * @param classname input
     * @param data      input
     * @return List of ErrorDomains
     */
    public static List<ErrorInfo> getStackTraceErrors(String classname, StackTraceElement[] data) {
        List<ErrorInfo> ret = new ArrayList<>();
        for (StackTraceElement var : data) {
            if (var.getClassName().contains(classname)) {
                ret.add(new ErrorInfo(var.getClassName(), var.getMethodName() + "(" + var.getLineNumber() + ")", var.getFileName(), ""));
            }
        }
        if (ret.size() > 0) {
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Validates the password
     * ----------------------
     * It contains at least 8 characters and at most 20 characters.
     * It contains at least one digit.
     * It contains at least one upper case alphabet.
     * It contains at least one lower case alphabet.
     * It contains at least one special character which includes !@#$%&*()-+=^.
     * It does n’t contain any white space.
     *
     * @param password input
     * @return boolean
     */
    public static boolean isValidPassword(String password, int lenString) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (isMandatoryValidString(password, lenString)) return p.matcher(password).matches();
        return false;
    }

    public static boolean isInvalidEmail(String email, int lenString) {
        String regex = "^(.+)@(.+)$";
        Pattern p = Pattern.compile(regex);

        if (!isMandatoryValidString(email, lenString)) return true;
        return !p.matcher(email).matches();
    }

    public static Map<String, String> getQueryParams(HttpServletRequest request) {
        //Query Param
        Map<String, String> retQueryParams = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] value = entry.getValue();
            if (value.length > 1) {
                Arrays.stream(value).forEach(s -> retQueryParams.put(entry.getKey(), s));
            } else {
                retQueryParams.put(entry.getKey(), value[0]);
            }
        }
        return retQueryParams;
    }

    /**
     * Stores in map the request info
     *
     * @param request HttpServletRequest
     * @return Map<String, Object>
     */
    public static Map<String, Object> logRequestHeadersToMap(HttpServletRequest request) {
        // Locate
        Map<String, String> retLocate = new LinkedHashMap<>();
        retLocate.put("Country", request.getLocale().getCountry());
        retLocate.put("DisplayCountry()", request.getLocale().getDisplayCountry());
        retLocate.put("Language", request.getLocale().getLanguage());
        retLocate.put("DisplayLanguage", request.getLocale().getDisplayLanguage());
        retLocate.put("DisplayName", request.getLocale().getDisplayName());
        retLocate.put("DisplayScript", request.getLocale().getDisplayScript());
        retLocate.put("DisplayVariant", request.getLocale().getDisplayVariant());
        retLocate.put("ISO3Country", request.getLocale().getISO3Country());
        retLocate.put("ISO3Language", request.getLocale().getISO3Language());
        retLocate.put("Variant", request.getLocale().getVariant());
        retLocate.put("LanguageTag", request.getLocale().toLanguageTag());
        // Others
        Map<String, Object> retOthers = new LinkedHashMap<>();
        retOthers.put("Method", request.getMethod());
        retOthers.put("Host", request.getRequestURL().toString());
        retOthers.put("AuthType", request.getAuthType());
        retOthers.put("RequestURI", request.getRequestURI());
        retOthers.put("RequestedSessionId", request.getRequestedSessionId());
        retOthers.put("RemoteUser", request.getRemoteUser());
        retOthers.put("RemotePort", String.valueOf(request.getRemotePort()));
        retOthers.put("RemoteAddr", request.getRemoteAddr());
        retOthers.put("ContextPath", request.getContextPath());
        retOthers.put("CharacterEncoding", request.getCharacterEncoding());
        retOthers.put("ContentType", request.getContentType());
        retOthers.put("PathInfo", request.getPathInfo());
        retOthers.put("PathTranslated", request.getPathTranslated());
        retOthers.put("Protocol", request.getProtocol());
        retOthers.put("ServletPath", request.getServletPath());
        retOthers.put("LocalAddr", request.getLocalAddr());
        retOthers.put("LocalName", request.getLocalName());
        retOthers.put("TrailerFields", request.getTrailerFields());

        // Cookies
        Map<String, String> retCookies = new LinkedHashMap<>();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                retCookies.put(cookie.getName(), cookie.getValue());
            }
        }
        // Headers
        Map<String, String> retHeaders = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String str = headerNames.nextElement();
            retHeaders.put(str, request.getHeader(str));
        }


        // Return Map
        Map<String, Object> retVal = new LinkedHashMap<>();
        retVal.put("Headers", retHeaders);
        retVal.put("Cookies", retCookies);
        retVal.put("Locate", retLocate);
        retVal.put("Others", retOthers);
        retVal.put("QueryParams", getQueryParams(request));

        return retVal;
    }

    /**
     * Stores in map the response info
     *
     * @param response HttpServletResponse
     * @return Map<String, Object>
     */
    public static Map<String, Object> logResponseHeadersToMap(HttpServletResponse response) {
        // Locate
        Map<String, String> retLocate = new LinkedHashMap<>();
        retLocate.put("Country", response.getLocale().getCountry());
        retLocate.put("DisplayCountry()", response.getLocale().getDisplayCountry());
        retLocate.put("Language", response.getLocale().getLanguage());
        retLocate.put("DisplayLanguage", response.getLocale().getDisplayLanguage());
        retLocate.put("DisplayName", response.getLocale().getDisplayName());
        retLocate.put("DisplayScript", response.getLocale().getDisplayScript());
        retLocate.put("DisplayVariant", response.getLocale().getDisplayVariant());
        retLocate.put("ISO3Country", response.getLocale().getISO3Country());
        retLocate.put("ISO3Language", response.getLocale().getISO3Language());
        retLocate.put("Variant", response.getLocale().getVariant());
        retLocate.put("LanguageTag", response.getLocale().toLanguageTag());
        // Others
        Map<String, Object> retOthers = new LinkedHashMap<>();
        retOthers.put("Status", String.valueOf(response.getStatus()));
        retOthers.put("CharacterEncoding", response.getCharacterEncoding());
        retOthers.put("ContentType", response.getContentType());
        retOthers.put("BufferSize", String.valueOf(response.getBufferSize()));
        retOthers.put("isCommitted", String.valueOf(response.isCommitted()));
        retOthers.put("TrailerFields", response.getTrailerFields());
        // Headers
        Map<String, String> retHeaders = new LinkedHashMap<>();
        Collection<String> headerNames = response.getHeaderNames();

        for (String key : headerNames) {
            retHeaders.put(key, response.getHeader(key));
        }

        //Return Map
        Map<String, Object> retVal = new LinkedHashMap<>();
        retVal.put("Headers", retHeaders);
        retVal.put("Locate", retLocate);
        retVal.put("Others", retOthers);

        return retVal;
    }

    /**
     * Returns the payload body
     *
     * @param jtwString String
     * @return String
     */
    public static String getJwtPayload(String jtwString) {
        if (isNotBlankString(jtwString)) {
            String regex = "[,]+";
            String[] ary = jtwString.split(regex);
            if (ary.length == 3) return String.valueOf(Base64.getDecoder().decode(ary[1]));
        }
        return "";
    }

    public static void validationObjectField(Object object, Map<String, ValidationFieldInfo> check)
            throws FieldFormatValidationException, NoSuchFieldException, IllegalAccessException {
        for (Map.Entry<String, ValidationFieldInfo> entry : check.entrySet()) {
            String key = entry.getKey().trim();
            Field field = object.getClass().getDeclaredField(key);
            field.setAccessible(true);
            //Field field = object.getClass().getField(key);
            ValidationFieldInfo validationFieldInfo = entry.getValue();
            if (validationFieldInfo.isMandatory()) {
                if (field.get(object) == null)  throw new FieldFormatValidationException(key);
                if (!isMandatoryValidString(field.get(object).toString().trim(), validationFieldInfo.getLen())) throw new FieldFormatValidationException(key);
            } else if (field.get(object) !=null && (field.get(object).toString().trim().length() > validationFieldInfo.getLen())) throw new FieldFormatValidationException(key);
            if (validationFieldInfo.isLowerCase()) field.set(object, field.get(object).toString().trim().toLowerCase());
            if (validationFieldInfo.isUpperCase()) field.set(object, field.get(object).toString().trim().toUpperCase());
        }
    }

    public static String getClassName(String className){
        String regex = "[.]+";
        String [] ret = className.split(regex);
        return ret[ret.length-1];
    }

    public static AuditInfoDto returnAuditInfo(HttpServletRequest httpServletRequest, AuditCache auditCache) {
        String str = (String) httpServletRequest.getAttribute("x-server-transaction-id");

        if (str == null) return new AuditInfoDto("N/A", "N/A", "N/A");

        AuditInfoDto retAudit = auditCache.getAudit(str);
        if (retAudit == null) return new AuditInfoDto("N/A", "N/A", "N/A");

        return retAudit;
    }

    public static String bufferReaderToString(BufferedReader reader)  {
        try {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }

            return content.toString();
        } catch (IOException ex) {
            return "";
        }
    }

    public static Object jsonStringToObject(String json){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> map = jsonStringToMap(json);
            String objType = (String) map.get("objectType");
            map.remove("objectType", objType);
            String strMap = new ObjectMapper().writeValueAsString(map);
            if (objType.equals("ErrorInfo")) {
                return (Object) objectMapper.readValue(strMap, ErrorInfo.class);
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Map<String, Object> jsonStringToMap(String json){
        Map<String, Object> map = new LinkedHashMap<>();

        try{
            return new ObjectMapper().readValue(json, Map.class);
        } catch (IOException ex) {
            return map;
        }
    }

    public static String ObjectToJsonString(Object object){
        try {
            Map map = new ObjectMapper().convertValue(object, Map.class);
            map.put("objectType", object.getClass().getSimpleName());
            return new ObjectMapper().writeValueAsString(map);
        } catch (IOException ex) {
            return "";
        }
    }
}
