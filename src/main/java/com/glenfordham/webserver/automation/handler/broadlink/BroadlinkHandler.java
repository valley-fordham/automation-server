package com.glenfordham.webserver.automation.handler.broadlink;

import com.glenfordham.utils.process.cmd.CommandLine;
import com.glenfordham.utils.process.cmd.CmdLineException;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.BroadlinkDevice;
import com.glenfordham.webserver.automation.jaxb.BroadlinkRequest;
import com.glenfordham.webserver.automation.jaxb.BroadlinkSignal;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.OutputStream;

/**
 * Broadlink actions require the broadlink CLI path (including Python path) and a number of parameters configured.
 * The required values will be dependent on your device.
 * See https://github.com/mjg59/python-broadlink for more information.
 */
public class BroadlinkHandler implements Handler {

    /**
     * Processes a broadlink type request. Matches request against configuration XML and triggers Broadlink action
     * on the device configured against the request
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        processRequest(parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst());
    }

    /**
     * Attempts to process the broadlink request.
     *
     * Checks that the request matches a supported request in the configuration file.
     * If it does, then invoke the broadlink action and device associated with that request name.
     *
     * @param incomingRequestName the name of the request to be actioned
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException if thread is interrupted while waiting for the process to complete, or if an error occurs when running broadlink CLI executable
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
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Check that the device associated with the request name is configured
        BroadlinkDevice device = config.getBroadlink().getDevices().stream()
                .filter(deviceEntry -> request.getBroadlinkDeviceName().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (device == null) {
            Log.error("Device name not configured: " + request.getBroadlinkDeviceName());
            return;
        }

        // Check if the incoming request matches a configured request name
        BroadlinkSignal signal = config.getBroadlink().getSignals().stream()
                .filter(signalEntry -> request.getSignalName().equalsIgnoreCase(signalEntry.getName()))
                .findFirst()
                .orElse(null);

        if (signal == null) {
            Log.error("Invalid signal name: " + incomingRequestName);
            return;
        }

        // Invoke the Broadlink executable and configured command line
        try {
            new CommandLine(config.getBroadlink().getCliPath()
                    + " --send " + signal.getCode()
                    + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress() + "\"").exec();
        } catch (CmdLineException e) {
            throw new HandlerException("Error occurred when executing Broadlink process", e);
        }
    }
}
