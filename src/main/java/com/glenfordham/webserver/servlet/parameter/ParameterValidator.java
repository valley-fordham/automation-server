package com.glenfordham.webserver.servlet.parameter;

/**
 * Interface for building URL parameter validation eg. ?token=x&token_2=y
 */
public interface ParameterValidator {

    /**
     * Standard interface for ParameterMap validation.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @return true if ParameterMap is valid
     */
    boolean isParameterMapValid(ParameterMap parameterMap);

    /**
     * Standard interface for parameter key validation.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @return true if all keys are valid
     * @throws ParameterException optional - may throw if there is an exception in Parameter handling
     */
    boolean areUrlParamKeysValid(ParameterMap parameterMap) throws ParameterException;

    /**
     * Standard interface for parameter validation.
     *
     * @param urlParams list of token values to be validated, belonging to a single parameter key
     * @return true if the list of values is valid
     * @throws ParameterException optional - may throw if there is an exception in Parameter handling
     */
    boolean areUrlParamsValid(ParameterList urlParams) throws ParameterException;
}
