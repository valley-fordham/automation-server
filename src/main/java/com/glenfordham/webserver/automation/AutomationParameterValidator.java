package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterList;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import com.glenfordham.webserver.servlet.parameter.ParameterValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class AutomationParameterValidator implements ParameterValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Validates all passed in parameters based on the Automation package requirements.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @return True is parameterMap is valid.
     */
    @Override
    public boolean isParameterMapValid(ParameterMap parameterMap) {
        try {
            return areUrlParamKeysValid(parameterMap)
                    && isAuthenticationTokenValid(parameterMap.get(Parameter.AUTHENTICATION_TOKEN.get()))
                    && isRequestTypeValid(parameterMap.get(Parameter.REQUEST_TYPE.get()))
                    && (parameterMap.containsKey(Parameter.REQUEST_NAME.get()) && !parameterMap.get(Parameter.REQUEST_NAME.get()).isEmpty());
        } catch (AutomationConfigException | ParameterException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Iterate over all parameter map keys and check if they are valid parameters against the Parameter Enum.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @return True if all keys are valid.
     */
    @Override
    public boolean areUrlParamKeysValid(ParameterMap parameterMap) throws ParameterException {
        // If proxy request type, URL parameter validation is handled in the Proxy handler.
        // Make sure that the minimum parameter values are present.
        if (parameterMap.containsKey(Parameter.REQUEST_TYPE.get())
                && parameterMap.get(Parameter.REQUEST_TYPE.get()).getFirst().equalsIgnoreCase(RequestType.PROXY.get())) {
            return Arrays.stream(Parameter.values()).allMatch(e->parameterMap.containsKey(e.get()));
        }
        return parameterMap.keySet().stream().noneMatch(key -> Arrays.stream(Parameter.values())
                .noneMatch(e -> e.get().equals(key)));
    }

    /**
     * Checks if the parameter values are valid for a given parameter key.
     *
     * @param urlParams List of token values to be validated, belonging to a single parameter key.
     * @return True if only one value for each key has been provided.
     */
    @Override
    public boolean areUrlParamsValid(ParameterList urlParams) {
        if (urlParams == null) {
            return false;
        }
        if (urlParams.size() != 1) {
            logger.error("Only one instance of a parameter key is allowed");
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided authentication token is valid.
     *
     * @param authenticationTokens List of parameter tokens.
     * @return True if there is only one authentication token, and the token value is correct.
     * @throws AutomationConfigException If unable to parse the configuration file to extract authentication config.
     * @throws ParameterException If authenticationTokens is empty.
     */
    private boolean isAuthenticationTokenValid(ParameterList authenticationTokens) throws AutomationConfigException, ParameterException {
        if (areUrlParamsValid(authenticationTokens)) {
            return Authenticator.authenticate(authenticationTokens.getFirst());
        } else {
            logger.error("Invalid authentication token");
            return false;
        }
    }

    /**
     * Checks if the provided request type is valid.
     *
     * @param requestTypes List of request types.
     * @return True if there is only one request type, and the token value is a valid request type.
     * @throws ParameterException If requestTypes is empty.
     */
    private boolean isRequestTypeValid(ParameterList requestTypes) throws ParameterException {
        if (areUrlParamsValid(requestTypes)) {
            for (RequestType requestTypeCheck : RequestType.values()) {
                if (requestTypes.getFirst().equals(requestTypeCheck.get())) {
                    return true;
                }
            }
        }
        logger.error("Invalid request type");
        return false;
    }
}
