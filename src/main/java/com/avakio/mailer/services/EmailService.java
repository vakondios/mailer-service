package com.avakio.mailer.services;

import com.avakio.mailer.dto.EmailMessageDto;
import com.avakio.mailer.dto.ResponseInfo;

public interface EmailService {
    Object sendEmail(EmailMessageDto emailMessageDto);
    Object sendToEmailQueue(ResponseInfo responseInfo, EmailMessageDto emailMessageDto);
}
