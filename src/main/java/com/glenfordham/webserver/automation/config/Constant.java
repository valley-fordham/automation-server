package com.glenfordham.webserver.automation.config;

/**
 * Defines constant values used by the {@link com.glenfordham.webserver.automation.config} package.
 */
public enum Constant {
    CONFIG_XSD(
            "config.xsd"
    );

    private final String text;

    Constant(String text) {
        this.text = text;
    }

    /**
     * Gets the text value of the Constant.
     *
     * @return The text value of the constant.
     */
    public String getText() {
        return text;
    }
}