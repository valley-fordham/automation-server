package com.glenfordham.webserver;

import com.glenfordham.webserver.config.CliParser;
import com.glenfordham.webserver.config.ConfigProperties;
import com.glenfordham.webserver.logging.Log;

public class Application {

    public static void main(String[] args) {
        try {
            // Set up environment variable for log4j
            System.setProperty("log4j.configurationFile", "com/glenfordham/webserver/log4j2-config.xml");
            Log.infoFormat("Application started under Java %s", System.getProperty("java.version"));

            // Load command-line arguments into configuration map and start if config loaded successfully
            ConfigProperties configProperties = new CliParser().loadConfig(args);
            if (configProperties != null) {
                TomcatServer.start(configProperties);
            }
        } catch (Exception e) {
             Log.error("Unknown error occurred", e);
        }
    }
}
