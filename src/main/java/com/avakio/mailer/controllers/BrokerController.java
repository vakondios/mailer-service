package com.avakio.mailer.controllers;

import com.avakio.mailer.dto.EmailMessageDto;
import com.avakio.mailer.dto.RequestHelperInfo;
import com.avakio.mailer.dto.ResponseInfo;
import com.avakio.mailer.properties.AppProperties;
import com.avakio.mailer.services.EmailService;
import com.avakio.mailer.utils.PrintUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "api/broker")
public class BrokerController {

    private final AppProperties appProperties;
    private final EmailService emailService;

    @Autowired
    public BrokerController(AppProperties appProperties,
                            EmailService emailService){

        this.emailService = emailService;
        this.appProperties = appProperties;
        if (log.isDebugEnabled()) log.debug("Controller Initialized.");
    }

    @Operation(security = @SecurityRequirement(name = "controllerBasicAuth")) //Swagger Configuration
    @PostMapping(value = "/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> sendEmail(
            @RequestBody EmailMessageDto emailMessageDto,
            @RequestAttribute("requestHelper") RequestHelperInfo requestHelperInfo) {

        PrintUtils.onController(requestHelperInfo);
        ResponseInfo responseInfo = new ResponseInfo(appProperties.getName(), appProperties.getVersion(), requestHelperInfo.getRequestURI());

        emailMessageDto.setTrxId(requestHelperInfo.getTransactionID());
        emailService.sendToEmailQueue(responseInfo, emailMessageDto);
        return new ResponseEntity<>(responseInfo, HttpStatus.valueOf(responseInfo.getStatus()));
    }
}
