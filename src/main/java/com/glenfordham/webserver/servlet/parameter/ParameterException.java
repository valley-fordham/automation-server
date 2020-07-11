package com.glenfordham.webserver.servlet.parameter;

public class ParameterException extends Exception {

    /**
     * Used for handling errors in classes within the parameters package
     *
     * @param message the message to be used for the Exception
     */
    public ParameterException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in classes within the parameters package
     *
     * @param message the message to be used for the Exception
     * @param e the exception to wrap when creating the new Exception
     */
    public ParameterException(String message, Exception e) {
        super(message, e);
    }
}
