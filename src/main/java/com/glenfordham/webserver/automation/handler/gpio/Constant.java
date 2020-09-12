package com.glenfordham.webserver.automation.handler.gpio;

/**
 * Defines constants used bv the {@link com.glenfordham.webserver.automation.handler.gpio} package.
 */
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
     * Gets the text value of the Constant.
     *
     * @return The text value of the Constant.
     */
    public String get() {
        return text;
    }
}