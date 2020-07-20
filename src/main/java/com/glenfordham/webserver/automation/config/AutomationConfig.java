package com.glenfordham.webserver.automation.config;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.logging.Log;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class AutomationConfig {

    // String constant used to save and retrieve Servlet configuration provided from command line
    public static final String CONFIG_LOCATION_KEY = "configLocationKey";

    private static Config config = null;
    private static boolean loaded = false;

    /**
     * Loads a configuration XML file and converts it into the JAXB representation
     *
     * @throws AutomationConfigException if an error occurs with loading, validation or conversion
     */
    public static void load(String configFileLocation) throws AutomationConfigException {
        // TODO: make thread safe, add new argument to control reloads
        if (!loaded) {
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
     * @return Config object
     * @throws AutomationConfigException if configuration file is not yet loaded
     */
    public static Config get() throws AutomationConfigException {
        if (config == null) {
            throw new AutomationConfigException("Configuration file unexpectedly not loaded.");
        }
        return config;
    }

    // use static method
    private AutomationConfig() {
    }
}
