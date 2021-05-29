package com.avakio.mailer.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ValidationFieldInfo extends AbstractDto{
    private boolean isMandatory;
    private int len;
    private boolean lowerCase;
    private boolean upperCase;
}
