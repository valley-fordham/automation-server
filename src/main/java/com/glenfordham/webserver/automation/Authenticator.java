package com.glenfordham.webserver.automation;

import org.apache.commons.lang3.StringUtils;

public class Authenticator {

    /**
     * Authenticates the provided token
     *
     * @param authenticationToken the token to be authenticated
     * @return true if the token passes authentication
     */
    static boolean authenticate(String authenticationToken) {
        return StringUtils.isNotBlank(authenticationToken) && authenticationToken.equals("winner");
    }

    // Use static method
    private Authenticator() {}
}
