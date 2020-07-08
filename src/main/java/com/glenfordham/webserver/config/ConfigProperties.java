package com.glenfordham.webserver.config;

import com.glenfordham.webserver.Log;

import java.util.EnumMap;

/**
 * Static class used for retrieving configuration properties across the application
 */
public class ConfigProperties {

    private static final EnumMap<Parameters, Object> configMap = new EnumMap<>(Parameters.class);

    private static ConfigProperties instance = null;

    /**
     * Adds the property and its value to the class
     *
     * @param param The parameter key to add
     * @param value The parameter's value
     */
    void addProperty(Parameters param, Object value) {
        configMap.put(param, value);
    }

    /**
     * Gets the value currently assigned to the passed in property parameter
     *
     * @param param The parameter with the value to retrieve
     * @return The parameter's value
     */
    public String getPropertyValue(Parameters param) {
        return (String) configMap.get(param);
    }

    /**
     * Gets the value currently assigned to the passed in property parameter as an Integer
     *
     * @param param The parameter with the value to retrieve
     * @return The parameter's value as an Integer
     */
    public Integer getPropertyValueAsInt(Parameters param) {
        try {
            return Integer.parseInt((String)configMap.get(param));
        } catch (Exception e) {
            Log.error("Unable to get property as Integer", e);
            return null;
        }
    }

    /**
     * Checks if the property is set
     *
     * @param param The parameter to check if set
     * @return true, if the parameter is set
     */
    public boolean isPropertySet(Parameters param) {
        return configMap.containsKey(param);
    }

    /**
     * Singleton pattern
     * Creates an instance of ConfigProperties if one does not exist
     *
     * @return the singleton instance of ConfigProperties
     */
    public static synchronized ConfigProperties getInstance() {
        if (instance == null) {
            instance = new ConfigProperties();
        }
        return instance;
    }

    // Constructor
    private ConfigProperties() {
        for (Parameters param : Parameters.values()) {
            addProperty(param, param.getDefaultValue());
        }
    }
}
