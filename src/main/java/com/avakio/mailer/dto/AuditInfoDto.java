package com.avakio.mailer.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AuditInfoDto extends AbstractDto {
    private String transaction_id;
    private String transaction_method;
    private String transaction_URI;
    private int userid;
    private String userKey;
    private int action;
    private int status;
    private int source;
    private boolean requestWasEncrypted;
    private boolean responseWasEncrypted;
    private boolean requestNeedsJwt;
    private boolean requestValidatedJwt;
    private boolean responseNeedsJwt;
    private Object jwtPayload;
    private long requestTimeStamp;
    private long respondTimeStamp;
    private Object requestParameters;
    private Object requestBody;
    private Object responseBody;
    private Object requestInfo;
    private Object responseInfo;

    public AuditInfoDto(String transaction_id, String transaction_method, String transaction_URI) {
        this.transaction_id = transaction_id;
        this.transaction_method = transaction_method;
        this.transaction_URI = transaction_URI;
        this.requestTimeStamp = System.currentTimeMillis();
    }

}