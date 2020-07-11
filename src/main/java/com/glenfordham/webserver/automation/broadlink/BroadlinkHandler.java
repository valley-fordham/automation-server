package com.glenfordham.webserver.automation.broadlink;

import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automation.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.utils.StringUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameters.ParameterException;
import com.glenfordham.webserver.servlet.parameters.ParameterMap;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

// TODO: make a handler interface
public class BroadlinkHandler {

    /**
     * Processes a broadlink type request. Matches request against configuration XML and triggers Broadlink action
     * on the device configured in the XML.
     *
     * Broadlink actions require the broadlink CLI path and a number of parameters configured, depending on your
     * broadlink device.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @throws InterruptedException if thread is interrupted while waiting for the process to complete
     * @throws IOException if an error occurs when running broadlink CLI executable
     * @throws JAXBException if unable to load configuration file
     * @throws ParameterException if unable to get request name from parameter
     */
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws InterruptedException, IOException, JAXBException, ParameterException {
        processRequest(parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst());
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
        try (ProcessWrapper processWrapper = new ProcessWrapper(
                Runtime.getRuntime().exec(config.getBroadlink().getCliPath()
                + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress()
                + "\" --send " + request.getSignalCode()))) {
            if (processWrapper.getProcess().waitFor() != 0) {
                Log.error(StringUtils.getStringFromStream(processWrapper.getProcess().getErrorStream()));
            }
        }
    }
}
