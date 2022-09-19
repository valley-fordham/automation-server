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
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterList;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * ProxyHandler processes proxy type requests, and allows the forward of a request to another Automation Server, or
 * other web service that processes URL parameters only. This handler will only forward URL parameters, and does not
 * forward other request body elements.
 */
public class ProxyHandler implements Handler {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Processes a proxy type request. Proxy requests are forwarded on to the configured destination.
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

        // Ensure Proxy element is present in config file
        if (config.getProxy() == null) {
            throw new HandlerException("No Proxy configuration in configuration XML");
        }

        // Check if the incoming request matches a configured request name
        ProxyRequest request = config.getProxy().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            logger.error("Invalid request name");
            return;
        }

        // Check that the device associated with the request name is configured
        ProxyHost host = config.getProxy().getHosts().stream()
                .filter(deviceEntry -> request.getHost().equalsIgnoreCase(deviceEntry.getName()))
                .findFirst()
                .orElse(null);

        if (host == null) {
            logger.error("Host name not configured: {}", request.getHost());
            return;
        }

        // If proxy request is for another automation server, check that all proxy parameters are present
        if (request.isForAutomationServer()
                && !Arrays.stream(ProxyParameterMapping.values()).allMatch(e->parameterMap.containsKey(e.getText())) ) {
            logger.error("Proxy request for another automation server does not contain all required URL parameters");
            return;
        }

        // Get list of parameters to forward in the proxy request
        ParameterMap forwardParameterMap;
        List<String> proxyParameters = request.getForwardParameters();
        // If proxy request is for another automation server, ensure standard parameters are forwarded in request
        if (request.isForAutomationServer()) {
            for (ProxyParameterMapping mapping : ProxyParameterMapping.values()) {
                proxyParameters.add(mapping.getText());
            }
        }
        // Forward the parameters as specified in configuration, others will be ignored
        forwardParameterMap = parameterMap.filterByList(request.getForwardParameters());

        // Remove 'proxy_' prefixes so request can be processed by the forward host
        if (request.isForAutomationServer()) {
            filterProxyPrefixes(forwardParameterMap);
        }

        // Send request to configured proxy host with configured forward parameters, and return response to original requester
        try {
            URL url = new URL(host.getScheme() + "://" + host.getFqdn() + ":" + host.getPort() + "/" + forwardParameterMap.getAsUrlString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(host.getConnectionTimeout());
            con.setReadTimeout(host.getReadTimeout());
            con.setInstanceFollowRedirects(false);
            clientOutput.write(StreamUtils.getString(con.getInputStream()).getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (Exception e) {
            throw new HandlerException(String.format("Error occurred when making proxy request. %s", e.getMessage()), e);
        }
    }

    /**
     * Remove the 'proxy_' prefix text from URL parameters in order to be forwarded to another automation server
     *
     * @param parameterMap The parameter map with keys to remove 'proxy_' prefix text from
     */
    private void filterProxyPrefixes(ParameterMap parameterMap) {
        for (ProxyParameterMapping mapping : ProxyParameterMapping.values()) {
            ParameterList tempParameterList = parameterMap.get(mapping.getText());
            parameterMap.remove(mapping.getText());
            parameterMap.put(mapping.getParameter().get(), tempParameterList);
        }
    }
}
