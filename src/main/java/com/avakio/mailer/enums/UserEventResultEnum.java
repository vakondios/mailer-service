package com.avakio.mailer.enums;

import java.util.stream.Stream;

/**
 * Enumeration with the user's result
 * for the user's audit log functionality
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
public enum UserEventResultEnum {
    executed(1),
    notAuthorised(2),
    error(3);

    private final int userEventResultEnum;

    UserEventResultEnum(int userEventResultEnum) {
        this.userEventResultEnum = userEventResultEnum;
    }

    public int getUserEventResultEnum() {
        return userEventResultEnum;
    }

    public static UserEventResultEnum of(int userEventResultEnum) {
        return Stream.of(UserEventResultEnum.values())
                .filter(p -> p.getUserEventResultEnum() == userEventResultEnum)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
