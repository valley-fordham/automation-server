package com.glenfordham.webserver.automation.config;

/**
 * An AutomationConfigException is thrown when an unexpected error is thrown by the
 * {@link com.glenfordham.webserver.automation.config} package.
 */
public class AutomationConfigException extends Exception {

    /**
     * Creates a new AutomationConfigException.
     *
     * @param message Message to be used for the Exception.
     */
    public AutomationConfigException(String message) {
        super(message);
    }

    /**
     * Creates a new AutomationConfigException.
     *
     * @param message Message to be used for the Exception.
     * @param e Exception to wrap when creating the new Exception.
     */
    public AutomationConfigException(String message, Exception e) {
        super(message, e);
    }
}
