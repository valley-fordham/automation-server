package com.glenfordham.webserver.config;

import com.glenfordham.webserver.logging.Log;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class CliParser {

    private final String exeName = (getClass().getPackage().getImplementationTitle() != null ? getClass().getPackage().getImplementationTitle() : "Web Server");

    /**
     * Parses CLI arguments and loads into the static ConfigProperties instance
     *
     * @param args application arguments
     * @return true if all required config has been loaded
     */
    public ConfigProperties loadConfig(String[] args) {
        // Add all arguments without setting the 'required' flag yet, so that the --help argument works
        Options options = new Options();

        // Add all available arguments to Options list and then set options as 'required' where needed
        Arrays.stream(Arguments.values()).forEach(arguments -> options.addOption(arguments.getName(), arguments.getLongName(), arguments.isArgValueRequired(), arguments.getHelpMessage()));
        Arrays.stream(Arguments.values()).filter(Arguments::getIsRequired).forEach(param -> options.addRequiredOption(param.getName(), param.getLongName(), param.isArgValueRequired(), param.getHelpMessage()));

        // Parse command line, this time checking that required arguments are present
        CommandLine cmd = parseArguments(options, args);

        // Check if the help argument has been provided or if a required argument is not provided - if so, display help and exit
        if (cmd == null || (cmd.hasOption(Arguments.HELP.getName()) || cmd.hasOption(Arguments.HELP.getLongName()))) {
            new HelpFormatter().printHelp(exeName, options);
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
     * Wrapper function for CLI argument parsing
     *
     * @param options a collection of options available to the application
     * @param args    the arguments provided to the application
     * @return CommandLine object representing the arguments passed by the user
     */
    CommandLine parseArguments(Options options, String[] args) {
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args, false);
        } catch (MissingOptionException | UnrecognizedOptionException parseException) {
            Log.info(parseException.getMessage());
        } catch (Exception e) {
            Log.error("Unable to parse arguments", e);
        }
        return cmd;
    }
}
