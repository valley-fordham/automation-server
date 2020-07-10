package com.glenfordham.webserver.automation;

public class Authenticator {

    static boolean authenticate(String authenticationToken) {
        return !authenticationToken.isBlank() && authenticationToken.equals("winner");
    }

    private Authenticator() {}
}
