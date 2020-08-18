package com.glenfordham.webserver.automation;

/**
 * Contains all supported URL parameters
 * <p>
 * eg. ?authentication_token=token&request_type=request_type&request_name=requestName
 */
public enum Parameter {
    AUTHENTICATION_TOKEN(
            "authentication_token"
    ),
    REQUEST_NAME(
            "request_name"
    ),
    REQUEST_TYPE(
            "request_type"
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