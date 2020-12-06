package com.glenfordham.webserver.servlet.parameter;

/**
 * A ParameterException is thrown when an unexpected error is thrown by the {@link com.glenfordham.webserver.servlet.parameter}
 * package.
 */
public class ParameterException extends Exception {

    /**
     * Used for handling errors in classes within the parameters package.
     *
     * @param message The message to be used for the Exception.
     */
    public ParameterException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in classes within the parameters package.
     *
     * @param message The message to be used for the Exception.
     * @param e The exception to wrap when creating the new Exception.
     */
    public ParameterException(String message, Exception e) {
        super(message, e);
    }
}
