package com.glenfordham.webserver.servlet.parameter;

/**
 * Interface for building URL parameter validation eg. ?token=x&amp;token_2=y
 */
public interface ParameterValidator {

    /**
     * Validates the passed in ParameterMap.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @return True, if ParameterMap is valid.
     */
    boolean isParameterMapValid(ParameterMap parameterMap);

    /**
     * Validates the passed in URL parameter keys.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @return True, if all keys are valid
     * @throws ParameterException Optional - may throw if there is an exception in Parameter handling.
     */
    boolean areUrlParamKeysValid(ParameterMap parameterMap) throws ParameterException;

    /**
     * Validates the values belonging to a single parameter key.
     *
     * @param urlParams List of token values to be validated, belonging to a single parameter key.
     * @return True, if the list of values is valid.
     * @throws ParameterException Optional - may throw if there is an exception in Parameter handling.
     */
    boolean areUrlParamsValid(ParameterList urlParams) throws ParameterException;
}
