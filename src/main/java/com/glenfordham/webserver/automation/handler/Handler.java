package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Standard interface for Handlers.
 */
public interface Handler {

    /**
     * Entry point for handler
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws JAXBException if unable to load configuration file
     * @throws ParameterException if unable to get request name from parameter
     */
    void start(ParameterMap parameterMap, OutputStream clientOutput) throws HandlerException, JAXBException, ParameterException, IOException, SAXException;
}
