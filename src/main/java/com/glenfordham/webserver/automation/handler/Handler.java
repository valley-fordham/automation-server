package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

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
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException;
}
