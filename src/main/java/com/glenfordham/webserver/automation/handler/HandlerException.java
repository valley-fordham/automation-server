package com.glenfordham.webserver.automation.handler;

/**
 * An HandlerException is thrown when an unexpected error is thrown by the {@link com.glenfordham.webserver.automation.handler}
 * package.
 */
public class HandlerException extends Exception {

    /**
     * Creates a new HandlerException.
     *
     * @param message the message to be used for the Exception
     */
    public HandlerException(String message) {
        super(message);
    }

    /**
     * Creates a new HandlerException.
     *
     * @param message Message to be used for the Exception.
     * @param e Exception to wrap when creating the new Exception.
     */
    public HandlerException(String message, Exception e) {
        super(message, e);
    }
}
