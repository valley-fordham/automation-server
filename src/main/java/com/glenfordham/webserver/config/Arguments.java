package com.glenfordham.webserver.config;

/**
 * Defines all supported application arguments.
 */
public enum Arguments {

    CONFIG_FILE(
            "c",
            true,
            true,
            "configFile",
            true,
            "the location of the config.xml file",
            null
    ),
    CONFIG_RELOAD(
            "r",
            false,
            true,
            "reload",
            false,
            "if present, config.xml will be loaded on every request",
            null
    ),
    DEBUG(
            "d",
            false,
            true,
            "debug",
            false,
            "if present, config.xml will be loaded on every request",
            null
    ),
    PORT(
            "p",
            false,
            true,
            "port",
            true,
            "sets the port to listen on  eg. 80",
            "80");

    private final String name;
    private final boolean isRequired;
    private final boolean isConfig;
    private final String longName;
    private final boolean isArgValueRequired;
    private final String helpMessage;
    private final String defaultValue;

    Arguments(String name, boolean isRequired, boolean isConfig, String longName, boolean isArgValueRequired, String helpMessage, String defaultValue) {
        this.name = name;
        this.isRequired = isRequired;
        this.isConfig = isConfig;
        this.longName = longName;
        this.isArgValueRequired = isArgValueRequired;
        this.helpMessage = helpMessage;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the name of the parameter.
     *
     * @return The parameter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets whether the argument is required or not.
     *
     * @return True if the argument is required.
     */
    public boolean getIsRequired() {
        return isRequired;
    }

    /**
     * Gets whether the argument is application configuration or not.
     *
     * @return True if the argument is application configuration.
     */
    public boolean getIsConfig() {
        return isConfig;
    }


    /**
     * Gets the long name variant of the argument.
     *
     * @return The argument long name variant.
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Gets whether an argument value is required for the argument.
     *
     * @return True if an argument value is required.
     */
    public boolean isArgValueRequired() {
        return isArgValueRequired;
    }

    /**
     * Gets the help message for the given argument, used by commons-cli
     *
     * @return The help message.
     */
    public String getHelpMessage() {
        return helpMessage;
    }

    /**
     * Gets the default value for the argument.
     *
     * @return The default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
