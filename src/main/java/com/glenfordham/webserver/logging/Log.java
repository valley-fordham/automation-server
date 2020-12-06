package com.glenfordham.webserver.logging;

import org.apache.logging.log4j.LogManager;

import java.util.Formatter;

/**
 * Defines a logging object against which all logging events can be called.
 */
public class Log {

    /**
     * Gets the log for the calling class.
     *
     * @return The log for the calling class.
     */
    private static org.apache.logging.log4j.Logger getLog() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace[2].getClassName().equals(Log.class.getCanonicalName())) {
            return LogManager.getLogger(stackTrace[3].getClassName());
        }
        return LogManager.getLogger(stackTrace[2].getClassName());
    }

    /**
     * Gets the log level for the calling class.
     *
     * @return The log level for the calling class.
     */
    public static String getLogLevel() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace[2].getClassName().equals(Log.class.getCanonicalName())) {
            return LogManager.getLogger(stackTrace[3].getClassName()).getLevel().name();
        }
        return LogManager.getLogger(stackTrace[2].getClassName()).getLevel().name();
    }


    /**
     * Writes a debug log event.
     *
     * @param message Debug message to be written.
     */
    public static void debug(String message) {
        getLog().debug(message);
    }


    /**
     * Writes a debug log event.
     *
     * @param cause Throwable to be written.
     */
    public static void debug(Throwable cause) {
        getLog().debug(cause.getMessage(), cause);
    }


    /**
     * Writes a debug log event.
     *
     * @param message Debug message to be written.
     * @param cause   Throwable to be written.
     */
    public static void debug(String message, Throwable cause) {
        getLog().debug(message, cause);
    }


    /**
     * Writes an error log event.
     *
     * @param message Error message to be written.
     */
    public static void error(String message) {
        getLog().error(message);
    }


    /**
     * Writes an error log event.
     *
     * @param cause Throwable to be written.
     */
    public static void error(Throwable cause) {
        getLog().error(cause.getMessage(), cause);
    }


    /**
     * Writes an error log event.
     *
     * @param message Error message to be written.
     * @param cause Throwable to be written.
     */
    public static void error(String message, Throwable cause) {
        getLog().error(message, cause);
    }


    /**
     * Writes an info log event.
     *
     * @param message Info message to be written.
     */
    public static void info(String message) {
        getLog().info(message);
    }

    /**
     * Writes an error log event.
     *
     * @param printfFormatMessage Info message to be written, containing placeholders. eg. %s
     * @param args Strings to be used in the formatter.
     */
    public static void infoFormat(String printfFormatMessage, Object ... args) {
        try (Formatter formatter = new Formatter()) {
            getLog().info(formatter.format(printfFormatMessage, args));
        }
    }


    /**
     * Writes a trace log event.
     *
     * @param message Trace message to be written.
     */
    public static void trace(String message) {
        getLog().trace(message);
    }


    // Constructor private to prevent misuse
    private Log() {
    }
}
