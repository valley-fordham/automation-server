package com.glenfordham.webserver.automation;

public class Authenticator {

    /**
     * Authenticates the provided token
     *
     * @param authenticationToken the token to be authenticated
     * @return true if the token passes authentication
     */
    static boolean authenticate(String authenticationToken) {
        return !authenticationToken.isBlank() && authenticationToken.equals("winner");
    }

    // Use static method
    private Authenticator() {}
}
