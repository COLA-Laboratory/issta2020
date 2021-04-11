package config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;

public class WAMConfigParser extends DefaultHandler {

    static final private boolean DEBUG=false;

    private String basedir;

    private WAMConfiguration config;

    public WAMConfigParser(File file) {
        config = new WAMConfiguration(file);
    }

    public WAMConfiguration getConfiguration() {
        return config;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() {
    }

    public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
        if (qName.equalsIgnoreCase("app")) {
            String app = attrs.getValue("name");
            config.setAppName(app);
            debug("Reading configuration file info for: " + app);
            basedir = attrs.getValue("basedir");
        } else if (qName.equalsIgnoreCase("class")) {
            String name=attrs.getValue("name");
            String target=attrs.getValue("target");
            if (name == "") {
                System.err.println("Name can not be undefined.");
            } else {
                config.addClass(name, target);
                debug("\tClass: " + name + ", " + target);
            }
            String isServletString = attrs.getValue("isServlet");
            if ((isServletString != null) && (isServletString.equalsIgnoreCase("true"))) {
                config.addServlet(target);
            }
        } else if (qName.equalsIgnoreCase("classpath")) {
            String dir = basedir + File.separator + attrs.getValue("dir");
            config.setClassPath(dir);
            debug("\tClass Path: " + dir);
        } else if (qName.equalsIgnoreCase("jar")) {
            String jar = attrs.getValue("path");
            File jarFile = new File(jar);
            if (!jarFile.isAbsolute()) {
                jar = basedir + File.separator + jar;
            }
            config.addRequiredJar(jar);
            debug("\tJar: " + jar);
        }
    }

    public void endElement(String namespaceURI, String sName, String qName) {

    }

    public void characters(char buf[], int offset, int len) throws SAXException {

    }

    private void debug(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
}