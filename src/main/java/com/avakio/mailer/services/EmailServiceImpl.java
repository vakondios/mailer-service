package com.avakio.mailer.services;

import com.avakio.mailer.components.JmsEmailProducerComponent;
import com.avakio.mailer.configurations.Constant;
import com.avakio.mailer.configurations.EmailConfiguration;
import com.avakio.mailer.configurations.ErrorEmailQueueConfiguration;
import com.avakio.mailer.dto.ErrorInfo;
import com.avakio.mailer.dto.ValidationFieldInfo;
import com.avakio.mailer.dto.EmailMessageDto;
import com.avakio.mailer.dto.ResponseInfo;
import com.avakio.mailer.exceptions.validation.FieldFormatValidationException;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailConfiguration emailConfiguration;
    private final JmsEmailProducerComponent jmsEmailProducerComponent;
    private final ErrorEmailQueueConfiguration errorEmailQueue;

    @Autowired
    public EmailServiceImpl(EmailConfiguration emailConfiguration,
                            JmsEmailProducerComponent jmsEmailProducerComponent,
                            ErrorEmailQueueConfiguration errorEmailQueue) {

        this.emailConfiguration = emailConfiguration;
        this.jmsEmailProducerComponent = jmsEmailProducerComponent;
        this.errorEmailQueue = errorEmailQueue;
        if (log.isDebugEnabled()) log.debug("Service Initialized.");
    }

    private void CatchException(Exception ex, int httpStatus, ResponseInfo responseInfo) {
        responseInfo.setErrors(CommonLib.getStackTraceErrors("com.avakio", ex.getStackTrace()));
        responseInfo.setError(ex.getMessage());
        responseInfo.setMessage(Constant.UNEXPECTED_ERROR);
        responseInfo.setStatus(httpStatus);
        responseInfo.setData(null);
        log.error(ex.getMessage());
    }

    private void validateEmail(EmailMessageDto emailMessageDto) throws FieldFormatValidationException, NoSuchFieldException, IllegalAccessException {
        // Return error type "Invalid_" + key + "_format"
        Map<String, ValidationFieldInfo> check = new LinkedHashMap<>();
        check.put("from", new ValidationFieldInfo(true, 50, true, false));
        check.put("fromName", new ValidationFieldInfo(true, 50, false, false));
        check.put("body", new ValidationFieldInfo(true, 5000, false, false));
        check.put("to", new ValidationFieldInfo(true, 50, true, false));
        check.put("subject", new ValidationFieldInfo(true, 50, false, false));
        CommonLib.validationObjectField(emailMessageDto, check);
    }

    public Object sendToEmailQueue(ResponseInfo responseInfo, EmailMessageDto emailMessageDto) {
        try {
            validateEmail(emailMessageDto);

            jmsEmailProducerComponent.sendEmail(emailMessageDto);

            responseInfo.setStatus(HttpStatus.CREATED.value());
            responseInfo.setData(emailMessageDto);
        } catch (FieldFormatValidationException ex) {
            responseInfo.setError(ex.getMessage());
            responseInfo.setMessage(Constant.SENDEMAIL_ERROR);
            responseInfo.setStatus(HttpStatus.BAD_REQUEST.value());
            if (log.isWarnEnabled()) log.warn(ex.getMessage());
        } catch (Exception ex) {
            CatchException(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), responseInfo);
        }

        return null;
    }

    public Object sendEmail(EmailMessageDto emailMessageDto) {
        try {
            validateEmail(emailMessageDto);

            emailConfiguration.sendHtmlEmail(emailMessageDto);

            if (log.isInfoEnabled()) log.info("[{}] => Email passed the validations and it is ready for send.", emailMessageDto.getTrxId());

        } catch (FieldFormatValidationException ex) {
            if (log.isWarnEnabled()) log.warn("[{}] => Email failed on validation before to send it.", emailMessageDto.getTrxId());

            try{
                errorEmailQueue.getQueue().put(new ErrorInfo("EmailServiceImpl", "sendEmail", "Email did not validate.", emailMessageDto.getTrxId()));
            } catch (Exception ex1) {
                log.error("System cannot send email for the trxId: {} from service class. Reason: {}", emailMessageDto.getTrxId(), ex1.getMessage());
            }
        } catch (Exception ex2) {
            log.error("[{}] => Email failed with reason: {}.", emailMessageDto.getTrxId(), ex2.getMessage());
            try{
                errorEmailQueue.getQueue().put(new ErrorInfo("EmailServiceImpl", "sendEmail", "Email process has error with error msg: " + ex2.getMessage() +" .", emailMessageDto.getTrxId()));
            } catch (Exception ex1) {
                log.error("System cannot send email for the trxId: {} from service class. Reason: {}", emailMessageDto.getTrxId(), ex1.getMessage());
            }
        }

        return null;
    }


}
