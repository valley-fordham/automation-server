package com.glenfordham.webserver.automation.handler.proxy;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.ProxyHost;
import com.glenfordham.webserver.automation.jaxb.ProxyRequest;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProxyHandler implements Handler {
    /**
     * Processes a proxy type request. Proxy requests are forwarded on to the configured destination.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();
        Config config = AutomationConfig.get();

        // Check if the incoming request matches a configured request name
        ProxyRequest request = config.getProxy().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Check that the device associated with the request name is configured
        ProxyHost host = config.getProxy().getHosts().stream()
                .filter(deviceEntry -> request.getHost().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (host == null) {
            Log.error("Host name not configured: " + request.getHost());
            return;
        }

        try {
            URL url = new URL(host.getScheme() + "://" + host.getFqdn() + ":" + host.getPort() + "/" + parameterMap.getAsUrlString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(host.getConnectionTimeout());
            con.setReadTimeout(host.getConnectionTimeout());
            con.setInstanceFollowRedirects(false);
            clientOutput.write(StreamUtils.getString(con.getInputStream()).getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (Exception e) {
            throw new HandlerException("Error occurred when making proxy request", e);
        }
    }
}
