package com.glenfordham.webserver.servlet.parameters;

public class ParameterException extends Exception {

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Exception e) {
        super(message, e);
    }
}
