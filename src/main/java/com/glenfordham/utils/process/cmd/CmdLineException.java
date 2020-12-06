package com.glenfordham.utils.process.cmd;

/**
 * A CmdLineException is thrown when an unexpected error is thrown by the {@link com.glenfordham.utils.process.cmd} package.
 */
public class CmdLineException extends Exception {

    /**
     * Used for handling errors in the CmdLine Class.
     *
     * @param message Message to be used for the Exception
     */
    public CmdLineException(String message) {
        super(message);
    }

    /**
     * Used for handling errors in the CmdLine Class.
     *
     * @param message Message to be used for the Exception.
     * @param e       Exception to wrap when creating the new Exception.
     */
    public CmdLineException(String message, Exception e) {
        super(message, e);
    }
}