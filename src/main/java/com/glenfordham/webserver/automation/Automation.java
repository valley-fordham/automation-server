package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.handler.broadlink.BroadlinkHandler;
import com.glenfordham.webserver.automation.handler.cmdline.CommandLineHandler;
import com.glenfordham.webserver.automation.handler.email.EmailHandler;
import com.glenfordham.webserver.automation.handler.gpio.GpioHandler;
import com.glenfordham.webserver.automation.handler.proxy.ProxyHandler;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.servlet.ServletContext;
import java.io.OutputStream;

public class Automation {

    /**
     * Attempts to process HTTP request.
     * Checks that URL parameters are valid, then identifies the request type and triage's the request to the appropriate
     * handler.
     *
     * @param parameterMap the parameters of the HTTP request
     */
    public void processHttpRequest(ServletContext context, ParameterMap parameterMap, OutputStream clientOutput) {
        try {
            AutomationConfig.load((String) context.getAttribute(AutomationConfig.CONFIG_LOCATION_KEY));
            AutomationParameterValidator parameterValidator = new AutomationParameterValidator();

            // If URL parameters are not valid, ignore the request
            if (!parameterValidator.isParameterMapValid(parameterMap)) {
                Log.debug("Invalid request: " + parameterMap.toString());
                return;
            }
            Log.debug("Valid request");

            RequestType requestType = RequestType.get(parameterMap.get(Parameter.REQUEST_TYPE.get()).getFirst());
            if (requestType != null) {
                switch (requestType) {
                    case BROADLINK:
                        new BroadlinkHandler().start(parameterMap, clientOutput);
                        return;
                    case CARPORT:
                    case CMD_LINE:
                        new CommandLineHandler().start(parameterMap, clientOutput);
                        return;
                    case EMAIL:
                        new EmailHandler().start(parameterMap, clientOutput);
                        return;
                    case GPIO:
                        new GpioHandler().start(parameterMap, clientOutput);
                        return;
                    case PROXY:
                        new ProxyHandler().start(parameterMap, clientOutput);
                        return;
                    default:
                }
            }
        } catch (AutomationConfigException acE) {
            Log.error("Error occurred loading/validating configuration file", acE);
        } catch (HandlerException hE) {
            Log.error("Error occurred within Handler", hE);
        } catch (Exception e) {
            Log.error("Unexpected error occurred", e);
        }
    }
}
