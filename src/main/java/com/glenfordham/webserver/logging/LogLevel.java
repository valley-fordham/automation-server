package com.glenfordham.webserver.logging;

/**
 * Defines the available log levels used by this application
 */
public enum LogLevel {
    INFO(
            "INFO"
    ),
    ERROR(
            "ERROR"
    ),
    DEBUG(
            "DEBUG"
    ),
    TRACE(
            "TRACE"
    );

    private final String text;

    LogLevel(String text) {
        this.text = text;
    }

    /**
     * Gets the Log level.
     *
     * @return The log level as text.
     */
    public String get() {
        return text;
    }
}