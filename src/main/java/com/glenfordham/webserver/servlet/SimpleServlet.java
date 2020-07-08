package com.glenfordham.webserver.servlet;

import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automationserver.jaxb.Config;
import com.glenfordham.webserver.config.ConfigProperties;
import com.glenfordham.webserver.config.Parameters;

import java.io.File;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

@WebServlet(
        name = "Servlet",
        urlPatterns = {"/"},
        loadOnStartup = 1
)
public class SimpleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ServletOutputStream out = resp.getOutputStream();

            File file = new File(ConfigProperties.getInstance().getPropertyValue(Parameters.CONFIG_FILE));
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Config config = (Config) jaxbUnmarshaller.unmarshal(file);

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
        } catch (Exception e) {
            Log.error("Unexpected error occurred in servlet", e);
        }
    }
}