package com.glenfordham.webserver.servlet;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.Automation;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "RequestArbiter",
        urlPatterns = {""},
        loadOnStartup = 1
)
public class RequestArbiter extends HttpServlet {

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
            Log.error("Unable to initialise configuration file at servlet start-up", e);
        }
    }

    private static final String GENERIC_OUTPUT =
            "<html lang=\"en\">\n" +
                    "\t<head>\n" +
                    "\t\t<title>Web Server</title>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t\t<div class='main'>\n" +
                    "      \t\tNothing to see here folks.\n" +
                    "\t\t</div>\n" +
            "\t</body>\n" +
            "</html>\n";

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
            Log.error("Unexpected error occurred in servlet", e);
        }
    }
}