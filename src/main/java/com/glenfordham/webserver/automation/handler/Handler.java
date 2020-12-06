package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.OutputStream;

/**
 * Interface for building Automation Server request handlers
 */
public interface Handler {

    /**
     * Entry point for a request handler.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @param clientOutput Client OutputStream, for writing a response.
     * @throws AutomationConfigException If unable to get configuration.
     * @throws HandlerException          If a generic Exception occurs when handling the request.
     * @throws ParameterException        If unable to get request name from parameter.
     */
    void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException;
}
