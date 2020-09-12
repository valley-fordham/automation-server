package com.glenfordham.webserver.automation;

/**
 * Contains all supported URL parameters
 * <p>
 * eg. ?authentication_token=token&amp;request_type=request_type&amp;request=requestName
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
     * Gets the name of the parameter
     *
     * @return The parameter name.
     */
    public String get() {
        return text;
    }

    /**
     * Gets the Enum form of the passed in text
     *
     * @param text String to be translated to an Enum.
     * @return The Enum - returns null on failure to match on an Enum.
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