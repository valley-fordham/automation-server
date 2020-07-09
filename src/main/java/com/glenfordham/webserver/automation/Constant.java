package com.glenfordham.webserver.automation;

public enum Constant {
    AUTHENTICATION_TOKEN(
            "authentication_token"
    ),
    REQUEST_TYPE(
            "request_type"
    ),
    REQUEST(
            "request"
    );

    private final String text;

    Constant(String text) {
        this.text = text;
    }

    /**
     * Returns the name of the parameter
     *
     * @return the parameter name
     */
    public String get() {
        return text;
    }

}