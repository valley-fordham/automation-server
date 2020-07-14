package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.automation.handler.BroadlinkHandler;
import com.glenfordham.webserver.automation.handler.CommandLineHandler;
import com.glenfordham.webserver.automation.handler.EmailHandler;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.xml.bind.JAXBException;
import java.io.OutputStream;

public class Automation {

    /**
     * Attempts to process HTTP request.
     * Checks that URL parameters are valid, then identifies the request type and triage's the request to the appropriate
     * handler.
     *
     * @param parameterMap the parameters of the HTTP request
     */
    public void processHttpRequest(ParameterMap parameterMap, OutputStream clientOutput) {
        try {
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
                    default:
                }
            }
        } catch (JAXBException jaxbE) {
            Log.error("Error occurred when loading config file", jaxbE);
        } catch (Exception e) {
            Log.error("Unexpected error occurred", e);
        }
    }
}
