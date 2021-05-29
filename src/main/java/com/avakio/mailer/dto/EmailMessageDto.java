package com.avakio.mailer.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto extends AbstractDto{
    private String trxId;
    private String from;
    private String fromName;
    private String body;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private List<String[]> attachment;
}
