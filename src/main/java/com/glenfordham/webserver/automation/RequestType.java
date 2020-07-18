package com.glenfordham.webserver.automation;

/**
 * Contains all supported request types
 *
 * eg. /?authentication_token=token&request_type=broadlink&request=requestName
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
     * Returns the text value of the RequestType
     *
     * @return text value
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
    public static RequestType get(String text) {
        for (RequestType requestType : RequestType.values()) {
            if (text.equalsIgnoreCase(requestType.get())) {
                return requestType;
            }
        }
        return null;
    }
}