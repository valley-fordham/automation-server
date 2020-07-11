package com.glenfordham.webserver.automation.handler;

import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automation.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
     * on the device configured in the XML.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws HandlerException a generic Exception occurs when handling the request
     * @throws JAXBException if unable to load configuration file
     * @throws ParameterException if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws HandlerException, JAXBException, ParameterException {
        try {
            processRequest(parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst());
        } catch (IOException | InterruptedException e) {
            // if an error occurs when running broadlink CLI executable,
            //  or if thread is interrupted while waiting for the process to complete
            throw new HandlerException(e.getMessage(), e);
        }
    }

    /**
     * Attempts to process the broadlink request.
     *
     * Checks that the request matches a supported request in the configuration file.
     * If it does, then invoke the broadlink action and device associated with that request name.
     *
     * @param incomingRequestName the name of the request to be actioned
     * @throws InterruptedException if thread is interrupted while waiting for the process to complete
     * @throws IOException if an error occurs when running broadlink CLI executable
     * @throws JAXBException if unable to load configuration file
     */
    private void processRequest(String incomingRequestName) throws InterruptedException, IOException, JAXBException {
        // Load configuration file on every attempt to ensure server does not need restarting when modifying config
        Config config = AutomationConfig.load();

        // Get all Device and Request elements, then attempt to process the request
        List<Config.Broadlink.Requests.Request> validRequestList = config.getBroadlink().getRequests().getRequest();
        List<Config.Broadlink.Devices.Device> validDeviceList =  config.getBroadlink().getDevices().getDevice();

        // Check if the incoming request matches a configured request name
        Config.Broadlink.Requests.Request request = validRequestList.stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // TODO: config validator?
        // Check that the device associated with the request name is configured
        Config.Broadlink.Devices.Device device = validDeviceList.stream()
                .filter(deviceEntry -> request.getBroadlinkDeviceName().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (device == null) {
            Log.error("Device name not configured: " + request.getBroadlinkDeviceName());
            return;
        }

        // Invoke the Broadlink executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        String executePath = config.getBroadlink().getCliPath()
                + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress()
                + "\" --send " + request.getSignalCode();
        Log.debug("Executing process: " + executePath);
        try (ProcessWrapper processWrapper = new ProcessWrapper(
                Runtime.getRuntime().exec(executePath))) {
            if (processWrapper.getProcess().waitFor() != 0) {
                Log.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
            }
        }
    }
}
