package com.glenfordham.webserver.automation.handler;

import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.jaxb.BroadlinkDevice;
import com.glenfordham.webserver.automation.jaxb.BroadlinkRequest;
import com.glenfordham.webserver.automation.jaxb.BroadlinkSignal;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Broadlink Handler
 *
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
     * @throws AutomationConfigException if unable to load configuration file
     * @throws HandlerException a generic Exception occurs when handling the request
     * @throws ParameterException if unable to get request name from parameter
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
     * @throws AutomationConfigException if unable to load configuration file
     * @throws HandlerException if thread is interrupted while waiting for the process to complete, or if an error occurs when running broadlink CLI executable
     */
    private void processRequest(String incomingRequestName) throws AutomationConfigException, HandlerException {
        // Load configuration file on every attempt to ensure server does not need restarting when modifying config
        Config config = AutomationConfig.load();

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

        // Invoke the Broadlink executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        String executePath = config.getBroadlink().getCliPath()
                + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress()
                + "\" --send " + signal.getCode();
        Log.debug("Executing process: " + executePath);
        try (ProcessWrapper processWrapper = new ProcessWrapper(
                Runtime.getRuntime().exec(executePath))) {
            if (processWrapper.getProcess().waitFor() != 0) {
                Log.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
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
