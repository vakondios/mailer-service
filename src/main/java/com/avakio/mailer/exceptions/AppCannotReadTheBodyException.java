package com.avakio.mailer.exceptions;

import com.avakio.mailer.dto.AuditInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class AppCannotReadTheBodyException extends HttpMessageNotReadableException {

    public AppCannotReadTheBodyException(HttpInputMessage httpInputMessage, AuditInfoDto audit, String classname, String function) {
        super("[" + audit.getTransaction_id() + "] => App cannot read the request body : [" + audit.getTransaction_method() + " - " + audit.getTransaction_URI() + "]. (" + classname + "/" + function + ")", httpInputMessage);
    }
}
