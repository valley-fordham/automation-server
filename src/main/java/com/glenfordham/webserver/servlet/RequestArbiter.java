package com.glenfordham.webserver.servlet;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.Automation;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(
        name = "RequestArbiter",
        urlPatterns = {""},
        loadOnStartup = 1
)
public class RequestArbiter extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Initialise config.xml and config handling at servlet startup
            AutomationConfig.load((String) this.getServletContext().getAttribute(AutomationConfig.CONFIG_LOCATION_KEY));
            if (this.getServletContext().getAttribute(AutomationConfig.CONFIG_RELOAD_KEY).equals(true)) {
                AutomationConfig.setConfigReload(true);
            }
        } catch (AutomationConfigException e) {
            if (this.getServletContext().getAttribute(AutomationConfig.CONFIG_DEBUG_KEY).equals(true)) {
                logger.error(String.format("Unable to initialise configuration file. %s", e.getMessage()), e);
            } else {
                logger.error(String.format("Unable to initialise configuration file. %s", e.getMessage()));
            }

            // If configuration reload is off, exit the application
            if (this.getServletContext().getAttribute(AutomationConfig.CONFIG_RELOAD_KEY).equals(false)) {
                System.exit(1);
            } else {
                logger.warn("Configuration will be attempted to be reloaded on next request.");
            }
        }
    }

    private static final String GENERIC_OUTPUT =
            """
            <html lang="en">
            \t<head>
            \t\t<title>Web Server</title>
            \t</head>
            \t<body>
            \t\t<div class='main'>
                  \t\tNothing to see here folks.
            \t\t</div>
            \t</body>
            </html>
            
            """;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        // Get output stream for client to be optionally used in various request handlers
        try (ServletOutputStream clientStream = resp.getOutputStream()) {

            // Pass context containing configuration, create ParameterMap from request ugly String[] map, and attempt to process the request
            new Automation().processHttpRequest(getServletContext(), new ParameterMap(req.getParameterMap()), clientStream);

            // If stream still ready after handler processing, assume nothing was written, and return generic response
            if (clientStream.isReady()) {
                StreamUtils.writeString(GENERIC_OUTPUT, clientStream);
            }
        } catch (Exception e) {
            if (this.getServletContext().getAttribute(AutomationConfig.CONFIG_DEBUG_KEY).equals(true)) {
                logger.error(String.format("Unexpected error occurred in servlet. %s", e.getMessage()), e);
            } else {
                logger.error(String.format("Unexpected error occurred in servlet. %s", e.getMessage()));
            }
        }
    }
}
