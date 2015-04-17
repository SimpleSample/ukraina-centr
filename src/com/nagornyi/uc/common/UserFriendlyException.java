package com.nagornyi.uc.common;

/**
 * @author Nagorny
 * Date: 29.04.14
 */
public class UserFriendlyException extends RuntimeException {

    private String userFriendlyMessage;

    public UserFriendlyException(String message) {
        super(message);
        userFriendlyMessage = message;
    }

    public UserFriendlyException(String message, Throwable cause) {
        super(message, cause);
        userFriendlyMessage = message;
    }

    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
}
