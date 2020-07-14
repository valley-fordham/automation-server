package com.glenfordham.webserver.config;

import com.glenfordham.webserver.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.util.Arrays;

public class CliParser {

    private static CliParser instance = null;
    private boolean loaded = false;
    private final String exeName = (getClass().getPackage().getImplementationTitle() != null ? getClass().getPackage().getImplementationTitle() : "Web Server");

    /**
     * Parses CLI arguments and loads into the static ConfigProperties instance
     *
     * @param args application arguments
     * @return true if all required config has been loaded
     */
    public synchronized boolean loadConfig(String[] args) {
        if (!loaded) {
            // Add all arguments without setting the 'required' flag yet, so that the --help argument works
            Options options = new Options();
            Arrays.stream(Arguments.values()).forEach(arguments -> options.addOption(arguments.getName(), arguments.getLongName(), arguments.isArgValueRequired(), arguments.getHelpMessage()));

            // Check if the help argument has been provided and, if so, display help and exit
            if (isHelpArgumentPresent(options, args)) {
                return false;
            }

            // Reprocess Arguments list and set options as 'required' where needed
            Arrays.stream(Arguments.values()).filter(Arguments::getIsRequired).forEach(param -> options.addRequiredOption(param.getName(), param.getLongName(), param.isArgValueRequired(), param.getHelpMessage()));

            // Parse command line, this time checking that required arguments are present
            CommandLine cmd = parseArguments(options, args, true);

            // Load command line arguments into ConfigProperties, set values where provided
            ConfigProperties configProperties = ConfigProperties.getInstance();
            if (cmd != null) {
                for (Arguments param : Arguments.values()) {
                    if (param.getIsConfig() && cmd.hasOption(param.getName())) {
                        configProperties.addProperty(param, cmd.getOptionValue(param.getName()));
                    }
                }
                loaded = true;
            } else {
                // This should never happen :)
                return false;
            }
        } else {
            Log.error("Unable to load CLI arguments, already loaded.");
        }
        return true;
    }

    private boolean isHelpArgumentPresent(Options options, String[] args) {
        CommandLine cmd = parseArguments(options, args, false);
        if (cmd == null) {
            return true;
        } else if (cmd.hasOption(Arguments.HELP.getName()) || cmd.hasOption(Arguments.HELP.getLongName())) {
            new HelpFormatter().printHelp(exeName, options);
            return true;
        }
        return false;
    }

    /**
     * Wrapper function for CLI argument parsing
     *
     * @param options         a collection of options available to the application
     * @param args            the arguments provided to the application
     * @param stopAtNonOption whether to stop if an unexpected argument is present
     * @return CommandLine object representing the arguments passed by the user
     */
    CommandLine parseArguments(Options options, String[] args, boolean stopAtNonOption) {
        try {
            return new DefaultParser().parse(options, args, stopAtNonOption);
        } catch (Exception e) {
            Log.error("Unable to parse arguments", e);
        }
        return null;
    }

    /**
     * Singleton pattern
     * Creates an instance of ConfigProperties if one does not exist
     *
     * @return the singleton instance of ConfigProperties
     */
    public static synchronized CliParser getInstance() {
        if (instance == null) {
            instance = new CliParser();
        }
        return instance;
    }

    // Ensure singleton pattern
    private CliParser() {
    }
}
