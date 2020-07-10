package com.glenfordham.webserver.config;

import com.glenfordham.webserver.Log;

import java.util.EnumMap;

/**
 * Static class used for retrieving configuration properties across the application
 */
public class ConfigProperties {

    private static final EnumMap<Arguments, Object> configMap = new EnumMap<>(Arguments.class);

    private static ConfigProperties instance = null;

    /**
     * Adds the property and its value to the class
     *
     * @param key The property key to add
     * @param value The property's value
     */
    void addProperty(Arguments key, Object value) {
        configMap.put(key, value);
    }

    /**
     * Gets the value currently assigned to the passed in property
     *
     * @param key The property key to retrieve the value of
     * @return The property's value
     */
    public String getPropertyValue(Arguments key) {
        return (String) configMap.get(key);
    }

    /**
     * Gets the value currently assigned to the passed in property as an Integer
     *
     * @param key The property key to retrieve the Integer value of
     * @return The property's value as an Integer
     */
    public Integer getPropertyValueAsInt(Arguments key) {
        try {
            return Integer.parseInt((String)configMap.get(key));
        } catch (Exception e) {
            Log.error("Unable to get property as Integer", e);
            return null;
        }
    }

    /**
     * Checks if the property is set
     *
     * @param key The property to check if set
     * @return true, if the property is set
     */
    public boolean isPropertySet(Arguments key) {
        return configMap.containsKey(key);
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
        for (Arguments param : Arguments.values()) {
            addProperty(param, param.getDefaultValue());
        }
    }
}
