package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.xml.bind.JAXBException;
import java.io.OutputStream;

public interface Handler {

    /**
     * Standard interface for Handlers.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws JAXBException if unable to load configuration file
     * @throws ParameterException if unable to get request name from parameter
     */
    void start(ParameterMap parameterMap, OutputStream clientOutput) throws HandlerException, JAXBException, ParameterException;
}
