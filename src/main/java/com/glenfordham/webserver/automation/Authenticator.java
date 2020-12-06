package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.TokenBehaviour;

/**
 * Defines an Authenticator object used to validate an authentication token.
 */
public class Authenticator {

    /**
     * Authenticates the provided token
     *
     * @param authenticationToken Token to be authenticated.
     * @return True if the token passes authentication.
     */
    static boolean authenticate(String authenticationToken) throws AutomationConfigException {
        Config config = AutomationConfig.get();
        String token = config.getAuthenticationToken().getToken();
        if (config.getAuthenticationToken().getBehaviour() == TokenBehaviour.SEED) {
            return true;
        } else {
            return !authenticationToken.isBlank() && authenticationToken.equals(token);
        }
    }

    // Use static method
    private Authenticator() {}
}
