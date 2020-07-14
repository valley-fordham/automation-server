package com.glenfordham.webserver.servlet;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.automation.Automation;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

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

            // Create ParameterMap from request ugly String[] map, and attempt to process the request
            new Automation().processHttpRequest(new ParameterMap(req.getParameterMap()), clientStream);

            // If stream still ready after handler processing, assume nothing was written, and return generic response
            // isReady() does not work correctly in Java8, so let's just wing it
            StreamUtils.writeString(GENERIC_OUTPUT, clientStream);
        } catch (Exception e) {
            Log.error("Unexpected error occurred in servlet", e);
        }
    }
}