package com.glenfordham.webserver.automation.config;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.logging.Log;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

/**
 * Represents configuration XML that complies to the Automation Server XML schema.
 */
public class AutomationConfig {

    // String constant used to save and retrieve Servlet configuration provided from command line
    public static final String CONFIG_LOCATION_KEY = "configLocation";
    public static final String CONFIG_RELOAD_KEY = "configReload";

    private static Config config = null;
    private static boolean loaded = false;
    private static boolean configReload = false;

    /**
     * Loads a configuration XML file and converts it into the JAXB representation.
     *
     * @param configFileLocation Path to the configuration file relative to the application run path.
     * @throws AutomationConfigException If an error occurs with loading, validation or conversion.
     */
    public static void load(String configFileLocation) throws AutomationConfigException {
        if (!loaded || configReload) {
            Log.info("Loading configuration XML");
            // Ensure that external files cannot be loaded
            SchemaFactory schemaFactory;
            try {
                schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

                // Load configuration file
                File configFile = new File(configFileLocation);

                // Load and validate config XML file against schema - use getResourceAsStream() so this code works when jar'd
                Schema schema = schemaFactory.newSchema(StreamUtils.getFile(AutomationConfig.class.getResourceAsStream(Constant.CONFIG_XSD.getText())));
                Validator validator = schema.newValidator();

                Source configFileAsXml = new StreamSource(configFile);
                validator.validate(configFileAsXml);

                // Convert config XML into Java object representation
                JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                config = (Config) jaxbUnmarshaller.unmarshal(configFile);
                loaded = true;
            } catch (Exception e) {
                throw new AutomationConfigException(e.getMessage(), e);
            }
        }
    }

    /**
     * Get the loaded Automation configuration file
     *
     * @return The Config object.
     * @throws AutomationConfigException If configuration file is not yet loaded.
     */
    public static Config get() throws AutomationConfigException {
        if (config == null) {
            throw new AutomationConfigException("Configuration file unexpectedly not loaded.");
        }
        return config;
    }

    /**
     * Sets the configReload value.
     *
     * @param value If true, configuration file will be loaded on every request. Should only be used in single-thread
     *              debug scenarios to ensure thread-safety.
     */
    public static void setConfigReload(boolean value) {
        configReload = value;
        Log.info("Configuration XML will reload on every request.");
    }

    // use static methods
    private AutomationConfig() {
    }
}
