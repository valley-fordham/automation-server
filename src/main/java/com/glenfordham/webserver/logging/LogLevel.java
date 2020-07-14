package com.glenfordham.webserver.logging;

public enum LogLevel {
    INFO(
            "INFO"
    ),
    ERROR(
            "ERROR"
    ),
    DEBUG(
            "DEBUG"
    );

    private final String text;

    LogLevel(String text) {
        this.text = text;
    }

    /**
     * Returns the Log level
     *
     * @return the parameter name
     */
    public String get() {
        return text;
    }
}