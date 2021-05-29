package com.avakio.mailer.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RequestHelperInfo extends AbstractDto {
    private String requestURI;
    private String methodName;
    private String transactionID;
}
