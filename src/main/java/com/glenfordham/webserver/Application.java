package com.glenfordham.webserver;

import com.glenfordham.webserver.config.CliParser;

public class Application {

    public static void main(String[] args) {
        try {
            // Set up environment variable for log4j
            System.setProperty("log4j.configurationFile", Application.class.getResource("log4j2-config.xml").getPath());
            Log.infoFormat("Application started under Java %s", System.getProperty("java.version"));

            // Load command-line arguments into configuration map and start
            CliParser.getInstance().loadConfig(args);
            TomcatServer.start();
        } catch (Exception e) {
             Log.error("Unknown error occurred", e);
        } finally {
            exit("Application exiting");
        }
    }

    public static void exit(String message, Exception e) {
        if (e != null) {
            Log.error(e);
        }
        Log.info(message);
        System.exit(e != null ? 1 : 0);
    }

    public static void exit(String message) {
        exit(message, null);
    }
}
