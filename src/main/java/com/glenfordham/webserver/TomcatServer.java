package com.glenfordham.webserver;

import com.glenfordham.webserver.config.ConfigProperties;
import com.glenfordham.webserver.config.Parameters;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TomcatServer {

    private static boolean started = false;

    /**
     * Starts the Tomcat server
     */
    static synchronized void start() {
        try {
            if (!started) {
                ConfigProperties configProperties = ConfigProperties.getInstance();

                File root = getRootFolder();
                Path tempPath = Files.createTempDirectory(configProperties.getPropertyValue(Parameters.TEMP_DIR_PREFIX));
                System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

                Tomcat tomcat = new Tomcat();
                tomcat.setBaseDir(tempPath.toString());
                tomcat.setPort(configProperties.getPropertyValueAsInt(Parameters.PORT));
                tomcat.getConnector();

                StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(root.getAbsolutePath(), "src/main/webapp/").getAbsolutePath());
                WebResourceRoot resources = new StandardRoot(ctx);
                resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File(root.getAbsolutePath(), "build/classes").getAbsolutePath(), "/"));
                ctx.setResources(resources);

                tomcat.start();
                started = true;
                tomcat.getServer().await();
            } else {
                Log.error("Unable to start Tomcat. Tomcat is already started.");
            }
        } catch (Exception e) {
            Application.exit("Unable to start Tomcat", e);
        }
    }

    private static File getRootFolder() throws URISyntaxException {
        File root;
        String runningJarPath = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
        int lastIndexOf = runningJarPath.lastIndexOf("/target/");
        if (lastIndexOf < 0) {
            root = new File("");
        } else {
            root = new File(runningJarPath.substring(0, lastIndexOf));
        }
        Log.infoFormat("Application root: %s", root.getAbsolutePath());
        return root;
    }

    private TomcatServer() {
    }
}
