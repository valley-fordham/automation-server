package com.glenfordham.webserver.config;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Defines a parser which can load all command-line arguments provided to this application and load them into a globally
 * accessible object.
 */
public class CliParser {

    private static final Logger logger = LogManager.getLogger();


    /**
     * Parses CLI arguments and loads into the static ConfigProperties instance.
     *
     * @param args Application arguments to be loaded into the ConfigProperties instance.
     * @return True if all required config has been loaded.
     */
    public ConfigProperties loadConfig(String[] args) {
        // Add all arguments without setting the 'required' flag yet
        Options options = new Options();

        // Add all available arguments to Options list and then set options as 'required' where needed
        Arrays.stream(Arguments.values()).forEach(arguments -> options.addOption(arguments.getName(), arguments.getLongName(), arguments.isArgValueRequired(), arguments.getHelpMessage()));
        Arrays.stream(Arguments.values()).filter(Arguments::getIsRequired).forEach(param -> options.addRequiredOption(param.getName(), param.getLongName(), param.isArgValueRequired(), param.getHelpMessage()));

        // Parse command line, this time checking that required arguments are present
        CommandLine cmd = parseArguments(options, args);

        // Check if the help argument has been provided or if a required argument is not provided - if so, display help and exit
        if (cmd == null) {
            new HelpFormatter().printHelp((getClass().getPackage().getImplementationTitle() != null ? getClass().getPackage().getImplementationTitle() : "Automation Server"), options);
        }

        // Load command line arguments into ConfigProperties, set values where provided
        ConfigProperties configProperties = new ConfigProperties();
        if (cmd != null) {
            for (Arguments param : Arguments.values()) {
                if (param.getIsConfig() && cmd.hasOption(param.getName())) {
                    configProperties.addProperty(param, cmd.getOptionValue(param.getName()));
                }
            }
        } else {
            // If CLI help has been displayed, do not load config so application will exit
            return null;
        }

        return configProperties;
    }

    /**
     * Wrapper function for CLI argument parsing.
     *
     * @param options Collection of Options/Arguments available to the application.
     * @param args    Options/Arguments provided to the application.
     * @return A CommandLine object representing the arguments passed by the user.
     */
    CommandLine parseArguments(Options options, String[] args) {
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args, false);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return cmd;
    }
}
