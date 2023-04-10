package com.codedead.advanced.portchecker.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWindowController {
    private PortController portController;
    private SettingsController settingsController;
    private final Logger logger;

    /**
     * Initialize a new MainWindowController
     */
    public MainWindowController() {
        logger = LogManager.getLogger(MainWindowController.class);
        logger.info("Initializing new MainWindowController object");
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
            throw new NullPointerException("PortController cannot be null!");

        this.portController = portController;
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
            throw new NullPointerException("SettingsController cannot be null!");

        this.settingsController = settingsController;
    }
}
