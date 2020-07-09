package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automation.broadlink.BroadlinkHandler;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.config.ConfigProperties;
import com.glenfordham.webserver.config.Parameters;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Map;

public class AutomationHandler {

    final BroadlinkHandler broadlinkHandler = new BroadlinkHandler();

    public boolean processRequest(Map<String, String[]> requestParams) {
        try {
            // TODO: implement better URL parameters
            // Load configuration file
            File file = new File(ConfigProperties.getInstance().getPropertyValue(Parameters.CONFIG_FILE));
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Config config = (Config)jaxbUnmarshaller.unmarshal(file);

            if (!isAuthenticationTokenValid(requestParams.get(Constant.AUTHENTICATION_TOKEN.get()))
                    || !isRequestTypeValid(requestParams.get(Constant.REQUEST_TYPE.get()))) {
                return false;
            }
            Log.info("Valid request");

            if (requestParams.get(Constant.REQUEST_TYPE.get())[0].equals(RequestType.BROADLINK.get())) {
                broadlinkHandler.processRequest(requestParams);
            }
            return true;
        } catch (Exception e) {
            Log.error("Unexpected error occurred", e);
            return false;
        }
    }

    private boolean isUrlParamValid(String[] urlParam) {
        if (urlParam == null) {
            return false;
        }
        if (urlParam.length != 1) {
            Log.error("Only one instance of a parameter is allowed");
            return false;
        }
        return true;
    }

    private boolean isAuthenticationTokenValid(String[] authenticationToken) {
        if (isUrlParamValid(authenticationToken)) {
            String token = authenticationToken[0];
            return token.equals("winning");
        } else {
            Log.error("Invalid authentication token");
            return false;
        }
    }

    private boolean isRequestTypeValid(String[] requestType) {
        if (isUrlParamValid(requestType)) {
            for (RequestType requestTypeCheck : RequestType.values()) {
                if (requestType[0].equals(requestTypeCheck.get())) {
                    return true;
                }
            }
        }
        Log.error("Invalid request type" + (requestType != null ? ": " + requestType[0] : " - not provided"));
        return false;
    }
}
