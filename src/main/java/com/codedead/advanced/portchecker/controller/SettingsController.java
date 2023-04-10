package com.codedead.advanced.portchecker.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class SettingsController {

    private String propertiesFileLocation;
    private String propertiesResourceLocation;
    private Properties properties;
    private final Logger logger;

    /**
     * Initialize a new SettingsController
     *
     * @param fileLocation               The location of the file that contains the {@link Properties} object
     * @param propertiesResourceLocation The location of the default {@link Properties} object in the resources
     * @throws IOException When the {@link Properties} object could not be loaded
     */
    public SettingsController(final String fileLocation,
                              final String propertiesResourceLocation) throws IOException {
        logger = LogManager.getLogger(SettingsController.class);
        logger.info("Initializing new SettingsController object");

        setPropertiesFileLocation(fileLocation);
        setPropertiesResourceLocation(propertiesResourceLocation);

        try {
            properties = readPropertiesFile();
        } catch (final FileNotFoundException ex) {
            logger.error("Properties object could not be loaded", ex);
            createDefaultProperties();
            properties = readPropertiesFile();
        }
    }

    /**
     * Create the default {@link Properties} object from resources
     *
     * @throws IOException When the default {@link Properties} object could not be read from the application resources or a pre-existing {@link Properties} object could not be deleted
     */
    public void createDefaultProperties() throws IOException {
        logger.info("Attempting to create the default properties file");

        final Path propertiesPath = Paths.get(getPropertiesFileLocation());
        if (Files.exists(propertiesPath)) {
            logger.info("Default properties file already exists, deleting the previous version");
            Files.delete(propertiesPath);
        }

        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(getPropertiesResourceLocation())) {
            if (is != null) {
                logger.info("Creating default properties file at {}", getPropertiesFileLocation());
                try {
                    final Path p = Paths.get(new File(propertiesPath.toString()).getParent());
                    Files.createDirectories(p);
                } catch (final IOException ex) {
                    logger.error("Could not create the parent directories for the properties file", ex);
                }
                Files.copy(is, propertiesPath);
            } else {
                throw new IOException(String.format("Could not load default properties from application resources (%s)!", getPropertiesResourceLocation()));
            }
        }
    }

    /**
     * Get the resource location of the default {@link Properties} object
     *
     * @return The resource location of the default {@link Properties} object
     */
    public String getPropertiesResourceLocation() {
        return propertiesResourceLocation;
    }

    /**
     * Set the resource location of the default {@link Properties} object
     *
     * @param propertiesResourceLocation The resource location of the default {@link Properties} object
     */
    public void setPropertiesResourceLocation(final String propertiesResourceLocation) {
        if (propertiesResourceLocation == null)
            throw new NullPointerException("Properties resource location cannot be null!");
        if (propertiesResourceLocation.isEmpty())
            throw new IllegalArgumentException("Properties resource location cannot be empty!");

        this.propertiesResourceLocation = propertiesResourceLocation;
    }

    /**
     * Get the properties file location
     *
     * @return The properties file location
     */
    public String getPropertiesFileLocation() {
        return propertiesFileLocation;
    }

    /**
     * Set the properties file location
     *
     * @param propertiesFileLocation The properties file location
     */
    public void setPropertiesFileLocation(final String propertiesFileLocation) {
        if (propertiesFileLocation == null || propertiesFileLocation.isEmpty())
            throw new IllegalArgumentException("Properties file location cannot be null or empty!");

        this.propertiesFileLocation = propertiesFileLocation;
    }

    /**
     * Get the Properties object
     *
     * @return The Properties object
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Set the Properties object
     *
     * @param properties The properties object
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * Save the properties
     *
     * @throws IOException When the Properties object could not be stored
     */
    public void saveProperties() throws IOException {
        logger.info("Storing the Properties object");
        try (final FileOutputStream fos = new FileOutputStream(getPropertiesFileLocation())) {
            properties.store(fos, null);
        }
    }

    /**
     * Retrieve the Properties object
     *
     * @return The Properties object
     * @throws IOException When the properties file could not be loaded
     */
    public Properties readPropertiesFile() throws IOException {
        logger.info("Loading the Properties object");
        try (final FileInputStream fis = new FileInputStream(getPropertiesFileLocation())) {
            final Properties prop = new Properties();
            prop.load(fis);
            return prop;
        }
    }
}
