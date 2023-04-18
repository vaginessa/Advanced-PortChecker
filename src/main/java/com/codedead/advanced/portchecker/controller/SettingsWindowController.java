package com.codedead.advanced.portchecker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

public class SettingsWindowController {

    @FXML
    public ComboBox<String> cboLogLevel;
    @FXML
    private ComboBox<String> cboTheme;
    @FXML
    private ComboBox<String> cboLanguage;
    private SettingsController settingsController;
    private PortController portController;
    private ResourceBundle resourceBundle;
    private final Logger logger;

    /**
     * Intialize the SettingsWindowController
     */
    public SettingsWindowController() {
        this.logger = LogManager.getLogger(SettingsWindowController.class);

        logger.info("Initializing new SettingsWindowController object");
    }

    /**
     * Get the {@link SettingsController} object
     *
     * @return The {@link SettingsController} object
     */
    public SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Set the {@link SettingsController} object
     *
     * @param settingsController The {@link SettingsController} object
     */
    public void setSettingsController(final SettingsController settingsController) {
        if (settingsController == null)
            throw new NullPointerException("SettingsController cannot be null");

        this.settingsController = settingsController;
    }

    /**
     * Get the {@link PortController} object
     *
     * @return The {@link PortController} object
     */
    public PortController getPortController() {
        return portController;
    }

    /**
     * Set the {@link PortController} object
     *
     * @param portController The {@link PortController} object
     */
    public void setPortController(final PortController portController) {
        if (portController == null)
            throw new NullPointerException("PortController cannot be null");

        this.portController = portController;
    }

    /**
     * Get the {@link ResourceBundle} object
     *
     * @return The {@link ResourceBundle} object
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Set the {@link ResourceBundle} object
     *
     * @param resourceBundle The {@link ResourceBundle} object
     */
    public void setResourceBundle(final ResourceBundle resourceBundle) {
        if (resourceBundle == null)
            throw new NullPointerException("ResourceBundle cannot be null");

        this.resourceBundle = resourceBundle;
    }
}
