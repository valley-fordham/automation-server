package com.glenfordham.utils.process.cmd;

public class CmdLineException extends Exception {

    /**
     * Used for handling errors in the CmdLine Class
     *
     * @param message the message to be used for the Exception
     */
    public CmdLineException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in the CmdLine Class
     *
     * @param message the message to be used for the Exception
     * @param e       the exception to wrap when creating the new Exception
     */
    public CmdLineException(String message, Exception e) {
        super(message, e);
    }
}