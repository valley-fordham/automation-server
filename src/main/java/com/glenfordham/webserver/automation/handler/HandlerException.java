package com.glenfordham.webserver.automation.handler;

public class HandlerException extends Exception {

    /**
     * Used for handling errors in classes within the handler package
     *
     * @param message the message to be used for the Exception
     */
    public HandlerException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in classes within the handler package
     *
     * @param message the message to be used for the Exception
     * @param e the exception to wrap when creating the new Exception
     */
    public HandlerException(String message, Exception e) {
        super(message, e);
    }
}
