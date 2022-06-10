package com.ro.integration.aloha;

import java.io.*;
import java.util.Properties;

public class PropertyReader {

    private static Properties properties;

    public static Properties getPropertiesFromFile(String filePath) {
        if (properties == null) {
//            try (InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream(filePath)) {
            try (InputStream input = new FileInputStream(filePath)) {
                properties = new Properties();
                // load a properties file
                properties.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
        return properties;
    }
}
