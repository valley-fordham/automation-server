package com.glenfordham.webserver.automation.handler.cmdline;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.CommandLineRequest;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.apache.commons.lang3.BooleanUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * CommandLine handler is used for processing command prompt and terminal commands in the same way that they would
 * process when executed against the operating system.
 */
public class CommandLineHandler implements Handler {
    /**
     * Processes a Command Line type request. Matches request against configuration XML and triggers command defined
     * against the request name.
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
        if (config.getCommandLine() == null) {
            throw new HandlerException("No CommandLine configuration in configuration XML");
        }

        // Check if the incoming request matches a configured request name
        CommandLineRequest request = config.getCommandLine().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name");
            return;
        }

        // Invoke the executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        String executePath = request.getCommandLine();
        Log.debug("Executing process: " + executePath);
        try {
            try (ProcessWrapper processWrapper = new ProcessWrapper(
                    Runtime.getRuntime().exec(executePath))) {
                // This waitFor implementation continues to wait if nothing is written to STDOUT, so we use a 30-second timeout
                if (!processWrapper.getProcess().waitFor(30, TimeUnit.SECONDS)) {
                    // Write the error to the logs, but not to the client as they don't need to know the details
                    Log.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
                    Log.info(String.valueOf(request.isOutputReturned()));
                } else if (BooleanUtils.isTrue(request.isOutputReturned())) {
                    clientOutput.write(StreamUtils.getString(processWrapper.getProcess().getInputStream()).getBytes(StandardCharsets.UTF_8));
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
