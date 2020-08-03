package com.glenfordham.webserver.automation.handler.gpio;

import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.*;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * For Raspberry Pi's only. This interfaces with the GPIO process that sits on the PATH environment variable.
 */
public class GpioHandler implements Handler {
    /**
     * Processes a GPIO type request. Matches request against configuration XML and triggers configured GPIO action.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();
        Config config = AutomationConfig.get();

        // Ensure Gpio element is present in config file
        if (config.getGpio() == null) {
            throw new HandlerException("No Gpio configuration in configuration XML");
        }

        // Check if the incoming request matches a configured request name
        GpioRequest request = config.getGpio().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null || request.isCarportOnly()) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Process request and get response from Gpio command
        String gpioResponse = GpioPinControl.process(request);
        // If no read was requested, the response will be null
        if (gpioResponse != null) {
            try {
                clientOutput.write(gpioResponse.getBytes());
            } catch (IOException e) {
                throw new HandlerException("Unable to write response", e);
            }
        }
    }
}