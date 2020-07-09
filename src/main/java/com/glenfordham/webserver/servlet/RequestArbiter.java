package com.glenfordham.webserver.servlet;

import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automation.AutomationHandler;

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

    final AutomationHandler automationHandler = new AutomationHandler();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ServletOutputStream out = resp.getOutputStream();
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
            out.write(outputHtml.getBytes());
            out.flush();
            out.close();
            automationHandler.processRequest(req.getParameterMap());
        } catch (Exception e) {
            Log.error("Unexpected error occurred in servlet", e);
        }
    }
}