package com.glenfordham.webserver.config;

import java.util.EnumMap;

/**
 * Static class used for retrieving configuration properties across the application.
 */
public class ConfigProperties {

    private final EnumMap<Arguments, Object> configMap = new EnumMap<>(Arguments.class);

    /**
     * Creates a default ConfigProperties with default values pulled from Arguments enum.
     */
    public ConfigProperties() {
        for (Arguments param : Arguments.values()) {
            if (param.getDefaultValue() != null) {
                addProperty(param, param.getDefaultValue());
            }
        }
    }

    /**
     * Adds the property and its value to the class.
     *
     * @param key The property key to add.
     * @param value The property key's value.
     */
    void addProperty(Arguments key, Object value) {
        configMap.put(key, value);
    }

    /**
     * Gets the value currently assigned to the passed in property.
     *
     * @param key The property key to retrieve the value of.
     * @return The property's value.
     */
    public String getPropertyValue(Arguments key) {
        return (String) configMap.get(key);
    }

    /**
     * Gets the value currently assigned to the passed in property as an Integer.
     *
     * @param key The property key to retrieve the Integer value of.
     * @return The property's value as an Integer.
     */
    public Integer getPropertyValueAsInt(Arguments key) {
        return Integer.parseInt((String)configMap.get(key));
    }

    /**
     * Checks if the property is set.
     *
     * @param key The property to check if set.
     * @return True, if the property is set.
     */
    public boolean isPropertySet(Arguments key) {
        return configMap.containsKey(key);
    }
}
