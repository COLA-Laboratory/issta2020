package config;

import exceptions.AnalysisPropertiesException;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

public class AnalysisProperties {
    private Properties properties = new Properties();
    private String filename;

    public AnalysisProperties() {
    }

    public AnalysisProperties(String propertiesFilename) throws AnalysisPropertiesException {
        load(propertiesFilename);
    }

    public void load(String propertiesFilename) throws AnalysisPropertiesException {
        this.filename=propertiesFilename;
        File propertiesFile = new File(propertiesFilename);
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (Exception e) {
            AnalysisPropertiesException ape = new AnalysisPropertiesException();
            ape.setFileLocation(propertiesFile.getAbsolutePath());
            ape.initCause(e);
            throw ape;
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }


}