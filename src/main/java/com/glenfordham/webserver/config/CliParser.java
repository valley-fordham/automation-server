package com.glenfordham.webserver.config;

import com.glenfordham.webserver.Application;
import com.glenfordham.webserver.Log;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class CliParser {

    private static CliParser instance = null;
    private boolean loaded = false;
    private final String exeName = (getClass().getPackage().getImplementationTitle() != null ? getClass().getPackage().getImplementationTitle() : "Web Server");

    /**
     * Parses command line options and loads into the static ConfigProperties instance
     *
     * @param args application arguments
     */
    public synchronized void loadConfig(String[] args) {
        if (!loaded) {
            // Add all arguments without setting the 'required' flag yet, so that the --help parameter works
            Options options = new Options();
            Arrays.stream(Parameters.values()).forEach(parameters -> options.addOption(parameters.getName(), parameters.getLongName(), parameters.isArgValueRequired(), parameters.getHelpMessage()));

            // Check if the help argument has been provided and, if so, display help
            CommandLine cmd = parseParameters(options, args, false);
            if (cmd.hasOption(Parameters.HELP.getName()) || cmd.hasOption(Parameters.HELP.getLongName())) {
                new HelpFormatter().printHelp(exeName, options);
                Application.exit("");
            }

            // Reprocess Parameters list and set options as 'required' where needed
            Arrays.stream(Parameters.values()).filter(Parameters::getIsRequired).forEach(param -> options.addRequiredOption(param.getName(), param.getLongName(), param.isArgValueRequired(), param.getHelpMessage()));

            // Re-parse command line, this time checking that required arguments are present
            cmd = parseParameters(options, args, true);

            // Load command line arguments into ConfigProperties, set values where provided
            ConfigProperties configProperties = ConfigProperties.getInstance();
            if (cmd != null) {
                for (Parameters param : Parameters.values()) {
                    if (param.getIsConfig() && cmd.hasOption(param.getName())) {
                        configProperties.addProperty(param, cmd.getOptionValue(param.getName()));
                    }
                }
                loaded = true;
            } else {
                // This should never happen :)
                Application.exit("Command-line arguments unable to initialise.");
            }
        } else {
            Log.error("Unable to load CLI arguments, already loaded.");
        }
    }

    /**
     * Wrapper function for command line parameter parsing
     *
     * @param options         a collection of options available to the application
     * @param args            the arguments provided to the application
     * @param stopAtNonOption whether to stop if an unexpected argument is present
     * @return CommandLine object representing the arguments passed by the user
     */
    CommandLine parseParameters(Options options, String[] args, boolean stopAtNonOption) {
        try {
            return new DefaultParser().parse(options, args, stopAtNonOption);
        } catch (Exception e) {
            Application.exit(e.getMessage());
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

    private CliParser() {
    }
}
