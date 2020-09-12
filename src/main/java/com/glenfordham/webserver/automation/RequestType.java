package com.glenfordham.webserver.automation;

/**
 * Contains all supported request types
 * <p>
 * eg. /?authentication_token=token&amp;request_type=broadlink&amp;request=requestName
 */
public enum RequestType {

    BROADLINK(
            "broadlink"
    ),
    CARPORT(
            "carport"
    ),
    CMD_LINE(
            "command_line"
    ),
    EMAIL(
            "email"
    ),
    GPIO(
            "gpio"
    ),
    PROXY(
            "proxy"
    );

    private final String text;

    RequestType(String text) {
        this.text = text;
    }

    /**
     * Gets the text value of the RequestType.
     *
     * @return The text value.
     */
    public String get() {
        return text;
    }

    /**
     * Returns the Enum form of the passed in text.
     *
     * @param text String to be translated to an Enum.
     * @return The Enum - returns null on failure to match on an Enum.
     */
    public static RequestType get(String text) {
        for (RequestType requestType : RequestType.values()) {
            if (text.equalsIgnoreCase(requestType.get())) {
                return requestType;
            }
        }
        return null;
    }
}