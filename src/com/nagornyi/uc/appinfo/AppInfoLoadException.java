package com.nagornyi.uc.appinfo;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class AppInfoLoadException extends Exception {

    public AppInfoLoadException() {
    }

    public AppInfoLoadException(String message) {
        super(message);
    }

    public AppInfoLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppInfoLoadException(Throwable cause) {
        super(cause);
    }
}
