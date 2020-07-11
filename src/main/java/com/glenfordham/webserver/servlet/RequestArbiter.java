package com.glenfordham.webserver.servlet;

import com.glenfordham.webserver.Log;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get output stream for client to be optionally used in various request handlers
            ServletOutputStream clientStream = resp.getOutputStream();
            new Automation().processHttpRequest(new ParameterMap(req.getParameterMap()), clientStream);

            // If stream still ready after handler processing, assume nothing was written, and return generic response
            if (clientStream.isReady()) {
                final String outputHtml = "<html lang=\"en\">\n" +
                        "\t<head>\n" +
                        "\t\t<title>Web Server</title>\n" +
                        "\t</head>\n" +
                        "\t<body>\n" +
                        "\t\t<div class='main'>\n" +
                        "      \t\tNothing to see here folks.\n" +
                        "\t\t</div>\n" +
                        "\t</body>\n" +
                        "</html>\n";
                clientStream.write(outputHtml.getBytes());
                clientStream.flush();
                clientStream.close();
            }
        } catch (Exception e) {
            Log.error("Unexpected error occurred in servlet", e);
        }
    }
}