package com.avakio.mailer.dto;

import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInfo extends AbstractDto {

    private String appTitle;
    private String appVersion;
    private Long timestamp;
    private Integer status;
    private String message;
    //Error
    private String error;
    private List<ErrorInfo> errors;
    private String stackTrace;
    private String path;
    private String authToken;
    private Object data;
    private Object data2;


    public ResponseInfo(String appTitle, String appVersion) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
        this.timestamp = new Date().getTime();
        this.status = 200;
    }

    public ResponseInfo(String appTitle, String appVersion, String path) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
        this.timestamp = new Date().getTime();
        this.path = path;
        this.status = 200;
    }

}
