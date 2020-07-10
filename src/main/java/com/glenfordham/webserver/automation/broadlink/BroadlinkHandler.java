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
import java.util.List;

public class BroadlinkHandler {

    Config config;
    String broadlinkCliPath;
    String incomingRequestName;
    List<Config.Broadlink.Requests.Request> validRequestList;
    List<Config.Broadlink.Devices.Device> validDeviceList;

    /**
     * Processes a Broadlink request type
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @throws JAXBException      thrown if error occurs when loading the config XML
     * @throws ParameterException thrown if error occurs parsing parameterMap
     */
    public void start(ParameterMap parameterMap) throws JAXBException, InterruptedException, IOException, ParameterException {
        config = AutomationConfig.load();
        broadlinkCliPath = config.getBroadlink().getCliPath();
        incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();

        // Get all Device and Request elements, and if the incoming request matches a configured one, process the request
        validDeviceList = config.getBroadlink().getDevices().getDevice();
        validRequestList = config.getBroadlink().getRequests().getRequest();
        // TODO: this is redundant
        if (validRequestList.stream().anyMatch(validRequest -> incomingRequestName.equalsIgnoreCase(validRequest.getName()))) {
            processRequest();
        }
    }

    private void processRequest() throws InterruptedException, IOException {
        Config.Broadlink.Requests.Request request = validRequestList.stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // TODO: config validator?
        Config.Broadlink.Devices.Device device = validDeviceList.stream()
                .filter(deviceEntry -> request.getBroadlinkDeviceName().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (device == null) {
            Log.error("Invalid device name: " + request.getBroadlinkDeviceName());
            return;
        }

        try (ProcessWrapper processWrapper = new ProcessWrapper(Runtime.getRuntime().exec(broadlinkCliPath
                + " --device \"" + device.getDeviceCode() + " " + device.getIpAddress() + " " + device.getMacAddress()
                + "\" --send " + request.getSignalCode()))) {
            if (processWrapper.getProcess().waitFor() != 0) {
                Log.error(StringUtils.getStringFromStream(processWrapper.getProcess().getErrorStream()));
            }
        }
    }
}
