package com.glenfordham.webserver.logging;

import org.apache.logging.log4j.LogManager;

import java.util.Formatter;

public class Log {

    /**
     * Gets the log for the calling class
     *
     * @return the log for the calling class
     */
    private static org.apache.logging.log4j.Logger getLog() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace[2].getClassName().equals(Log.class.getCanonicalName())) {
            return LogManager.getLogger(stackTrace[3].getClassName());
        }
        return LogManager.getLogger(stackTrace[2].getClassName());
    }

    /**
     * Gets the log level for the calling class
     *
     * @return the log level for the calling class
     */
    public static String getLogLevel() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace[2].getClassName().equals(Log.class.getCanonicalName())) {
            return LogManager.getLogger(stackTrace[3].getClassName()).getLevel().name();
        }
        return LogManager.getLogger(stackTrace[2].getClassName()).getLevel().name();
    }


    /**
     * Writes to log as debug
     *
     * @param message debug message to be written
     */
    public static void debug(String message) {
        getLog().debug(message);
    }


    /**
     * Writes to log as debug
     *
     * @param cause throwable to be written
     */
    public static void debug(Throwable cause) {
        getLog().debug(cause.getMessage(), cause);
    }


    /**
     * Writes to log as debug
     *
     * @param message debug message to be written
     * @param cause   throwable to be written
     */
    public static void debug(String message, Throwable cause) {
        getLog().debug(message, cause);
    }


    /**
     * Writes to log as error
     *
     * @param message error message to be written
     */
    public static void error(String message) {
        getLog().error(message);
    }


    /**
     * Writes to log as error
     *
     * @param cause throwable to be written
     */
    public static void error(Throwable cause) {
        getLog().error(cause.getMessage(), cause);
    }


    /**
     * Writes to log as error
     *
     * @param message error message to be written
     * @param cause throwable to be written
     */
    public static void error(String message, Throwable cause) {
        getLog().error(message, cause);
    }


    /**
     * Writes to log as info
     *
     * @param message info message to be written
     */
    public static void info(String message) {
        getLog().info(message);
    }

    /**
     * Writes to log as info
     *
     * @param printfFormatMessage info message to be written, containing placeholders eg. %s
     * @param args strings to be used in the formatter
     */
    public static void infoFormat(String printfFormatMessage, Object ... args) {
        try (Formatter formatter = new Formatter()) {
            getLog().info(formatter.format(printfFormatMessage, args));
        }
    }


    /**
     * Writes to log as trace
     *
     * @param message trace message to be written
     */
    public static void trace(String message) {
        getLog().trace(message);
    }


    // Constructor private to prevent misuse
    private Log() {
    }
}
