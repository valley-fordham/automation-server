package com.glenfordham.webserver;

import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.config.Arguments;
import com.glenfordham.webserver.config.ConfigProperties;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.BindException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines an embedded Tomcat Server to be used as the web listener for web requests
 */
public class TomcatServer {

    private static final Logger logger = LogManager.getLogger();

    private static boolean started = false;

    /**
     * Starts the Tomcat server. Only one running Tomcat instance is supported.
     */
    static synchronized void start(ConfigProperties configProperties) {
        try {
            if (!started) {
                File root = getRootFolder();
                Path tempPath = Files.createTempDirectory("automation-server");
                System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

                Tomcat tomcat = new Tomcat();
                tomcat.setBaseDir(tempPath.toString());
                tomcat.setPort(configProperties.getPropertyValueAsInt(Arguments.PORT));
                tomcat.getConnector();

                StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(root.getAbsolutePath()).getAbsolutePath());

                // Load Servlet config into Servlet Context for accessibility
                ctx.getServletContext().setAttribute(AutomationConfig.CONFIG_LOCATION_KEY, configProperties.getPropertyValue(Arguments.CONFIG_FILE));
                ctx.getServletContext().setAttribute(AutomationConfig.CONFIG_RELOAD_KEY, configProperties.isPropertySet(Arguments.CONFIG_RELOAD));
                ctx.getServletContext().setAttribute(AutomationConfig.CONFIG_DEBUG_KEY, configProperties.isPropertySet(Arguments.DEBUG));

                // Check if running within a jar and use appropriate resource set object
                String runningUriPath = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                WebResourceRoot resources = new StandardRoot(ctx);
                resources.addPreResources(runningUriPath.toUpperCase().endsWith(".JAR")
                        ? new JarResourceSet(resources, "/WEB-INF/classes", new File(runningUriPath).getAbsolutePath(), "/")
                        : new DirResourceSet(resources, "/WEB-INF/classes", new File(runningUriPath).getAbsolutePath(), "/"));
                ctx.setResources(resources);

                logger.info("Application root: {}", root.getAbsolutePath());
                logger.info("Listening port: {}", configProperties.getPropertyValue(Arguments.PORT));
                tomcat.start();
                started = true;
                tomcat.getServer().await();
            } else {
                logger.error("Unable to start Tomcat. Tomcat is already started.");
            }
        } catch (Exception e) {
            // Annoyingly, BindExceptions are nested inside LifeCycle exceptions
            if (e.getCause() instanceof BindException) {
                logger.error("Unable to start. A process is already bound to port.");
            } else {
                if (configProperties.isPropertySet(Arguments.DEBUG)) {
                    logger.error(e.getMessage(), e);
                } else {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Gets the root folder of the Tomcat directory.
     *
     * @return A File containing the absolute root path.
     * @throws URISyntaxException If unable to convert location to URI.
     */
    private static File getRootFolder() throws URISyntaxException {
        File root;
        String runningJarPath = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace("\\\\", "/");
        int lastIndexOf = runningJarPath.lastIndexOf("/target/");
        if (lastIndexOf < 0) {
            root = new File("");
        } else {
            root = new File(runningJarPath.substring(0, lastIndexOf));
        }
        return root;
    }

    // Ensure only one TomcatServer is created using static start() method
    private TomcatServer() {
    }
}
