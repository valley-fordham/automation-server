package com.glenfordham.webserver.automation.config;

public enum Constant {
    CONFIG_XSD(
            "config.xsd"
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
    public String getText() {
        return text;
    }
}