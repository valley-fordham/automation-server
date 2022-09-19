package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.handler.broadlink.BroadlinkHandler;
import com.glenfordham.webserver.automation.handler.carport.CarportHandler;
import com.glenfordham.webserver.automation.handler.cmdline.CommandLineHandler;
import com.glenfordham.webserver.automation.handler.email.EmailHandler;
import com.glenfordham.webserver.automation.handler.gpio.GpioHandler;
import com.glenfordham.webserver.automation.handler.proxy.ProxyHandler;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

/**
 * Defines an entry point used to handle a HTTP request to be processed by the Automation Server.
 */
public class Automation {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Attempts to process HTTP request.
     * Checks that URL parameters are valid, then identifies the request type and triage's the request to the
     * appropriate handler.
     *
     * @param context      ServletContext used to retrieve configuration.
     * @param parameterMap Parameters of the HTTP request.
     * @param clientOutput OutputStream which will be delivered to the client.
     */
    public void processHttpRequest(ServletContext context, ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        AutomationConfig.load((String) context.getAttribute(AutomationConfig.CONFIG_LOCATION_KEY));
        AutomationParameterValidator parameterValidator = new AutomationParameterValidator();

        // If URL parameters are not valid, ignore the request
        if (!parameterValidator.isParameterMapValid(parameterMap)) {
            logger.debug("Invalid request");
            return;
        }
        logger.debug("Valid request");

        RequestType requestType = RequestType.get(parameterMap.get(Parameter.REQUEST_TYPE.get()).getFirst());
        if (requestType != null) {
            switch (requestType) {
                case BROADLINK -> new BroadlinkHandler().start(parameterMap, clientOutput);
                case CARPORT -> new CarportHandler().start(parameterMap, clientOutput);
                case CMD_LINE -> new CommandLineHandler().start(parameterMap, clientOutput);
                case EMAIL -> new EmailHandler().start(parameterMap, clientOutput);
                case GPIO -> new GpioHandler().start(parameterMap, clientOutput);
                case PROXY -> new ProxyHandler().start(parameterMap, clientOutput);
            }
        }
    }
}
