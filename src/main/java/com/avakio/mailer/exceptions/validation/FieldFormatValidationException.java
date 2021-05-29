package com.avakio.mailer.exceptions.validation;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of field format error
 */
public class FieldFormatValidationException extends AuthenticationException {

    private static final long serialVersionUID = -4313297951095453558L;

    public FieldFormatValidationException(String fieldName) {
        super("Invalid".concat(fieldName).concat("Format"));
    }
}