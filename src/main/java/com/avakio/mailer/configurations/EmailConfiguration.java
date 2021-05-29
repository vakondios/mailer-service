package com.avakio.mailer.configurations;

import com.avakio.mailer.dto.EmailMessageDto;
import com.avakio.mailer.dto.ErrorInfo;
import com.avakio.mailer.properties.EmailProperties;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.mail.*;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Slf4j
public class EmailConfiguration {

    private final EmailProperties emailProperties;
    private final ErrorEmailQueueConfiguration errorEmailQueue;
    private final Properties props = new Properties();
    private final transient ConnectionListener listener;
    private transient Session session;
    private transient Transport transport;

    @Autowired
    public EmailConfiguration(EmailProperties emailProperties,
                              ErrorEmailQueueConfiguration errorEmailQueue) {

        this.errorEmailQueue = errorEmailQueue;
        this.emailProperties = emailProperties;
        init();

        listener = new ConnectionListener() {
            public void opened(ConnectionEvent e) {
                if (log.isInfoEnabled()) log.info("The connection to SMTP Server opened");
            }
            public void disconnected(ConnectionEvent e) {
                if (log.isInfoEnabled()) log.info("The connection to SMTP Server disconnected");
            }
            public void closed(ConnectionEvent e) {
                if (log.isInfoEnabled()) log.info("The connection to SMTP Server closed");
                transport = null;
            }
        };

        connect();

        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }

    private void connect() {
        try {
            session = Session.getDefaultInstance(props);
            transport = session.getTransport(emailProperties.getTransportProtocol());
            transport.addConnectionListener(listener);
            transport.connect(
                    props.getProperty("mail.smtp.host"),
                    emailProperties.getSmtpPort(),
                    props.getProperty("mail.smtp.user"),
                    props.getProperty("mail.smtp.password"));
        } catch (Exception ex) {
            transport = null;
            log.error(ex.getMessage());
        }
    }

    private void init() {
        props.put("mail.smtp.timeout", emailProperties.getConnectionTimeOut());
        props.put("mail.smtp.connectiontimeout", emailProperties.getConnectionTimeOut());
        props.put("mail.smtp.user", emailProperties.getUsername());
        props.put("mail.smtp.password", emailProperties.getPassword());
        props.put("mail.smtp.host", emailProperties.getHost());
        props.put("mail.smtp.port", emailProperties.getSmtpPort());
        props.put("mail.smtp.from", emailProperties.getFrom());
        props.put("mail.host", "localhost");
        props.put("mail.transport.protocol", emailProperties.getTransportProtocol());
        props.put("mail.smtp.starttls.enable",emailProperties.isStarttlsEnable());
        props.put("mail.smtp.starttls.required",emailProperties.isStarttlsRequired());
    }

    @Async("EmailSendTaskExecutor")
    public void sendHtmlEmail(EmailMessageDto email) {

        try {
            InternetAddress fromAddress = new InternetAddress(email.getFrom(), email.getFromName());
            InternetAddress toAddress = new InternetAddress(email.getTo());
            InternetAddress ccAddress = CommonLib.isNotBlankString(email.getCc()) ? new InternetAddress(email.getCc()) : null;
            InternetAddress bccAddress = CommonLib.isNotBlankString(email.getBcc()) ? new InternetAddress(email.getBcc()) : null;

            Message message = new MimeMessage(session);
            message.setFrom(fromAddress);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            if (ccAddress != null) message.setRecipient(Message.RecipientType.CC, ccAddress);
            if (bccAddress != null) message.setRecipient(Message.RecipientType.BCC, bccAddress);
            message.setSubject(email.getSubject());
            message.setContent(email.getBody(), "text/html; charset=utf-8");
            message.saveChanges();

            //Check Connectivity
            if (transport == null || !transport.isConnected()) connect();

            transport.sendMessage(message, message.getAllRecipients());
            if (log.isInfoEnabled())
                    log.info("[{}] => Email sent successfully for the current transaction", email.getTrxId());
        } catch (Exception ex) {
            try {
                errorEmailQueue.getQueue().put(new ErrorInfo("EmailConfiguration", "SendHtmlEmail", ex.getMessage(), email.getTrxId()));
                if (log.isWarnEnabled()) log.warn("[{}] => Email did not send successfully for the current transaction", email.getTrxId());
            } catch (Exception exec) {
                log.error("System cannot send email for the trxId: {}. Reason: {}", email.getTrxId(), exec.getMessage());
            }
        }
    }
}
