package com.glenfordham.webserver.automation.config;

public class AutomationConfigException extends Exception {
    /**
     * Used for handling errors in classes within the config package
     *
     * @param message the message to be used for the Exception
     */
    public AutomationConfigException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in classes within the config package
     *
     * @param message the message to be used for the Exception
     * @param e the exception to wrap when creating the new Exception
     */
    public AutomationConfigException(String message, Exception e) {
        super(message, e);
    }
}
