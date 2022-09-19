package com.glenfordham.webserver.automation.config;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Represents configuration XML that complies to the Automation Server XML schema.
 */
public class AutomationConfig {

    private static final Logger logger = LogManager.getLogger();

    // Used to save and retrieve Servlet configuration provided from command line
    public static final String CONFIG_LOCATION_KEY = "configLocation";
    public static final String CONFIG_RELOAD_KEY = "configReload";
    public static final String CONFIG_DEBUG_KEY = "configDebug";

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
            logger.info("Loading configuration XML");
            try {
                // Load configuration file
                File configFile = new File(configFileLocation);
                if (!configFile.exists()) {
                    throw new AutomationConfigException("Configuration XML file does not exist.");
                }
                validateConfig(configFile);

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
        logger.info("Configuration XML will reload on every request.");
    }

    private static void validateConfig(final File configFile) throws AutomationConfigException {
        SchemaFactory schemaFactory;
        schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            // Load and validate config XML file against schema - use getResourceAsStream() so this code works when jar'd
            Schema schema = schemaFactory.newSchema(StreamUtils.getFile(AutomationConfig.class.getResourceAsStream(Constant.CONFIG_XSD.getText())));
            Validator validator = schema.newValidator();
            Source configFileAsXml = new StreamSource(configFile);

            validator.validate(configFileAsXml);
        } catch (IOException | SAXException e) {
            throw new AutomationConfigException(e.getMessage(), e);
        }
    }

    // use static methods
    private AutomationConfig() {
    }
}
