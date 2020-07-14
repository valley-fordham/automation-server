package com.glenfordham.webserver.automation;

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
     * @throws JAXBException if XML cannot be unmarshalled
     */
    public static Config load() throws JAXBException, SAXException, IOException {
        // Load configuration file from location passed in from CLI argument
        File configFile = new File(ConfigProperties.getInstance().getPropertyValue(Arguments.CONFIG_FILE));
        File schemaFile = new File(AutomationConfig.class.getResource("config.xsd").getPath());
        Source configFileAsXml = new StreamSource(configFile);
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.validate(configFileAsXml);

        JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Config) jaxbUnmarshaller.unmarshal(configFile);
    }

    // use static method
    private AutomationConfig() {}
}
