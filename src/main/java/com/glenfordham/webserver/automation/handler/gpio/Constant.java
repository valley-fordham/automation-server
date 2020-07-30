package com.glenfordham.webserver.automation.handler.gpio;

public enum Constant {
    GPIO_WRITE(
            "write"
    ),
    GPIO_READ(
            "read"
    );

    private final String text;

    Constant(String text) {
        this.text = text;
    }

    /**
     * Returns the text value of the Constant
     *
     * @return text value
     */
    public String get() {
        return text;
    }
}