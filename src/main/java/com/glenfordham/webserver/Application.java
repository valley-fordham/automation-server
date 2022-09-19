package com.glenfordham.webserver;

import com.glenfordham.webserver.config.Arguments;
import com.glenfordham.webserver.config.CliParser;
import com.glenfordham.webserver.config.ConfigProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

public class Application {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            // Set up environment variable for log4j
            System.setProperty("log4j.configurationFile", "com/glenfordham/webserver/log4j2-config.xml");
            logger.info("Application started under Java {}", System.getProperty("java.version"));

            // Load command-line arguments into configuration map and start if config loaded successfully
            ConfigProperties configProperties = new CliParser().loadConfig(args);

            if (configProperties != null) {
                if (configProperties.isPropertySet(Arguments.DEBUG)) {
                    Configurator.setRootLevel(Level.DEBUG);
                }

                File configFile = new File(configProperties.getPropertyValue(Arguments.CONFIG_FILE));
                if (!configFile.exists()) {
                    logger.error("Configuration XML file does not exist.");
                    System.exit(1);
                }

                TomcatServer.start(configProperties);
            }
        } catch (Exception e) {
             logger.error(e.getMessage(), e);
        }
    }
}
