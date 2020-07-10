package com.glenfordham.webserver.automation;

import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.config.Arguments;
import com.glenfordham.webserver.config.ConfigProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class AutomationConfig {

    public static Config load() throws JAXBException {
        // Load configuration file from location passed in from CLI argument
        File file = new File(ConfigProperties.getInstance().getPropertyValue(Arguments.CONFIG_FILE));
        JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Config) jaxbUnmarshaller.unmarshal(file);
    }

    private AutomationConfig() {}
}
