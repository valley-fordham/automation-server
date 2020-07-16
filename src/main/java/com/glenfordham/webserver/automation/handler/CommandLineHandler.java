package com.glenfordham.webserver.automation.handler;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.jaxb.CommandLineRequest;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.IOException;
import java.io.OutputStream;

public class CommandLineHandler implements Handler {
    /**
     * Processes a Command Line type request. Matches request against configuration XML and triggers command defined
     * against the request name
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to load configuration file
     * @throws HandlerException a generic Exception occurs when handling the request
     * @throws ParameterException if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();

        // Load configuration file on every attempt to ensure server does not need restarting when modifying config
        Config config = AutomationConfig.load();

        // Check if the incoming request matches a configured request name
        CommandLineRequest request = config.getCommandLine().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Invoke the executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        String executePath = request.getCommandLine();
        Log.debug("Executing process: " + executePath);
        try {
            try (ProcessWrapper processWrapper = new ProcessWrapper(
                    Runtime.getRuntime().exec(executePath))) {
                if (processWrapper.getProcess().waitFor() != 0) {
                    Log.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
                }
            }
        } catch (InterruptedException iE) {
            Log.error("Interrupted execution of process", iE);
            Thread.currentThread().interrupt();
            throw new HandlerException(iE.getMessage(), iE);
        } catch (IOException e) {
            throw new HandlerException(e.getMessage(), e);
        }
    }
}
