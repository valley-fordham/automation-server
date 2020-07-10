package com.glenfordham.webserver.automation;

public enum Parameter {
    AUTHENTICATION_TOKEN(
            "authentication_token"
    ),
    REQUEST_TYPE(
            "request_type"
    ),
    REQUEST_NAME(
            "request"
    );

    private final String text;

    Parameter(String text) {
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

    /**
     * Returns the Enum form of the passed in text
     *
     * @param text the String to be translated to an Enum
     * @return the Enum - returns null on failure
     */
    public static Parameter get(String text) {
        for (Parameter parameter : Parameter.values()) {
            if (text.equalsIgnoreCase(parameter.get())) {
                return parameter;
            }
        }
        return null;
    }

}