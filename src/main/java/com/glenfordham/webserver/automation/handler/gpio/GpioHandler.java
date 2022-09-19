package com.glenfordham.webserver.automation.handler.gpio;

import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.GpioRequest;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This handler interfaces with the GPIO process that sits on the PATH environment variable. For Raspberry Pi's only.
 */
public class GpioHandler implements Handler {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Processes a GPIO type request. Matches request against configuration XML and triggers configured GPIO action.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @param clientOutput Client OutputStream, for writing a response.
     * @throws AutomationConfigException If unable to get configuration.
     * @throws HandlerException          If a generic Exception occurs when handling the request.
     * @throws ParameterException        If unable to get request name from parameter.
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
            logger.error("Invalid request name");
            return;
        }

        // Process request and get response from Gpio command
        String gpioResponse = GpioPinControl.process(request);
        // If no read was requested, the response will be null
        if (gpioResponse != null) {
            try {
                clientOutput.write(gpioResponse.getBytes());
            } catch (IOException e) {
                throw new HandlerException(String.format("Unable to write response. %s", e.getMessage()), e);
            }
        }
    }
}
