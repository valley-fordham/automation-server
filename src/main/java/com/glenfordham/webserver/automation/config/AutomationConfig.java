package com.glenfordham.webserver.automation.config;

import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.config.Arguments;
import com.glenfordham.webserver.config.ConfigProperties;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class AutomationConfig {

    /**
     * Loads a configuration XML file and converts it into the JAXB representation
     *
     * @return the Config JAXB object
     * @throws AutomationConfigException if an error occurs with loading, validation or conversion
     */
    public static Config load() throws AutomationConfigException {
        // Ensure that external files cannot be loaded
        SchemaFactory schemaFactory;
        try {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            // Load configuration file
            File configFile = new File(ConfigProperties.getInstance().getPropertyValue(Arguments.CONFIG_FILE));

            // Load and validate config XML file against schema

            File schemaFile = new File(AutomationConfig.class.getResource(Constant.CONFIG_XSD.getText()).getPath());
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();

            Source configFileAsXml = new StreamSource(configFile);
            validator.validate(configFileAsXml);

            // Convert config XML into Java object representation
            JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Config) jaxbUnmarshaller.unmarshal(configFile);
        } catch (IOException | JAXBException | SAXException e) {
            throw new AutomationConfigException(e.getMessage(), e);
        }
    }

    // use static method
    private AutomationConfig() {}
}
