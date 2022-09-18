package com.glenfordham.webserver.automation.handler.broadlink;

import com.glenfordham.utils.process.cmd.CmdLineException;
import com.glenfordham.utils.process.cmd.CommandLine;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.BroadlinkDevice;
import com.glenfordham.webserver.automation.jaxb.BroadlinkRequest;
import com.glenfordham.webserver.automation.jaxb.BroadlinkSignal;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

/**
 * Broadlink actions require the broadlink CLI path (including Python path) and a number of parameters configured.
 * The required values will be dependent on your device.
 * @see <a href="https://github.com/mjg59/python-broadlink">python-broadlink on GitHub</a>
 */
public class BroadlinkHandler implements Handler {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Processes a broadlink type request. Matches request against configuration XML and triggers Broadlink action
     * on the device configured against the request.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @param clientOutput Client OutputStream, for writing a response.
     * @throws AutomationConfigException If unable to get configuration.
     * @throws HandlerException          If a generic Exception occurs when handling the request.
     * @throws ParameterException        If unable to get request name from parameter.
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        processRequest(parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst());
    }

    /**
     * Attempts to process the broadlink request.
     * Checks that the request matches a supported request in the configuration file.
     * If it does, then invoke the broadlink action and device associated with that request name.
     *
     * @param incomingRequestName Name of the request to be actioned.
     * @throws AutomationConfigException If unable to get configuration.
     * @throws HandlerException If thread is interrupted while waiting for the process to complete, or if an error occurs when running broadlink CLI executable.
     */
    private void processRequest(String incomingRequestName) throws AutomationConfigException, HandlerException {
        Config config = AutomationConfig.get();

        // Ensure Broadlink element is present in config file
        if (config.getBroadlink() == null) {
            throw new HandlerException("No Broadlink configuration in configuration XML");
        }

        // Check if the incoming request matches a configured request name
        BroadlinkRequest request = config.getBroadlink().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            logger.error("Invalid request name");
            return;
        }

        // Check that the device associated with the request name is configured
        BroadlinkDevice device = config.getBroadlink().getDevices().stream()
                .filter(deviceEntry -> request.getBroadlinkDeviceName().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (device == null) {
            logger.error("Device name not configured: {}", request.getBroadlinkDeviceName());
            return;
        }

        // Check if the incoming request matches a configured request name
        BroadlinkSignal signal = config.getBroadlink().getSignals().stream()
                .filter(signalEntry -> request.getSignalName().equalsIgnoreCase(signalEntry.getName()))
                .findFirst()
                .orElse(null);

        if (signal == null) {
            logger.error("Invalid signal name: {}", incomingRequestName);
            return;
        }

        // Invoke the Broadlink executable and configured command line
        try {
            new CommandLine(config.getBroadlink().getCliPath()
                    + " --send " + signal.getCode()
                    + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress() + "\"").exec();
        } catch (CmdLineException e) {
            throw new HandlerException(String.format("Error occurred when executing Broadlink process. %s", e.getMessage()), e);
        }
    }
}
