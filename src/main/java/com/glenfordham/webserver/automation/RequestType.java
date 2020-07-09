package com.glenfordham.webserver.automation;

public enum RequestType {

    BROADLINK(
            "broadlink"
    );

    private final String text;

    RequestType(String text) {
        this.text = text;
    }

    /**
     * Returns the text value of the RequestType
     *
     * @return text value
     */
    public String get() {
        return text;
    }
}