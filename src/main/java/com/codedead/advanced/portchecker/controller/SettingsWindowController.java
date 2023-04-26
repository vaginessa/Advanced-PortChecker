package com.codedead.advanced.portchecker.controller;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.codedead.advanced.portchecker.domain.NumberTextField;
import com.codedead.advanced.portchecker.utils.FxUtils;
import com.codedead.advanced.portchecker.utils.SharedVariables;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingsWindowController {

    @FXML
    public ComboBox<String> cboLogLevel;
    @FXML
    private NumberTextField ntfSocketTimeout;
    @FXML
    private NumberTextField ntfThreadPoolSize;
    @FXML
    private CheckBox chbScanAsync;
    @FXML
    private CheckBox chbAutoUpdate;
    @FXML
    private ComboBox<String> cboTheme;
    @FXML
    private ComboBox<String> cboLanguage;
    private SettingsController settingsController;
    private PortController portController;
    private ResourceBundle resourceBundle;
    private final Logger logger;

    /**
     * Initialize the SettingsWindowController
     */
    public SettingsWindowController() {
        this.logger = LogManager.getLogger(SettingsWindowController.class);

        logger.info("Initializing new SettingsWindowController object");
    }

    /**
     * FXML initialize method
     */
    @FXML
    private void initialize() {
        cboTheme.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            switch (newValue) {
                case "Dark" -> Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
                case "NordLight" -> Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
                case "NordDark" -> Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
                default -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            }
        });
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
        loadSettings();
    }

    /**
     * Load the settings into the UI
     */
    private void loadSettings() {
        final Properties properties = getSettingsController().getProperties();

        final boolean autoUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));
        final String locale = properties.getProperty("locale", "en-US");
        final String theme = properties.getProperty("theme", "Light");
        final String logLevel = properties.getProperty("logLevel", "INFO");
        final boolean scanAsync = Boolean.parseBoolean(properties.getProperty("scanAsync", "true"));
        int threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize", "-1"));
        final int socketTimeout = Integer.parseInt(properties.getProperty("socketTimeout", "2000"));

        final int localeIndex = switch (locale.toLowerCase()) {
            default:
            case "en-us":
                yield 0;
            case "fr-fr":
                yield 1;
            case "nl-nl":
                yield 2;
        };

        if (threadPoolSize == -1) {
            threadPoolSize = Runtime.getRuntime().availableProcessors();
        }

        chbAutoUpdate.setSelected(autoUpdate);
        cboLanguage.getSelectionModel().select(localeIndex);
        cboTheme.getSelectionModel().select(theme);
        cboLogLevel.getSelectionModel().select(logLevel);
        chbScanAsync.setSelected(scanAsync);
        ntfThreadPoolSize.setText(Integer.toString(threadPoolSize));
        ntfSocketTimeout.setText(Integer.toString(socketTimeout));
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

    /**
     * Show an information alert if a restart is required
     *
     * @param languageToMatch The language that needs to be matched to the combobox
     */
    private void showAlertIfLanguageMismatch(final String languageToMatch) {
        final String newLanguage = switch (cboLanguage.getSelectionModel().getSelectedIndex()) {
            case 1 -> "fr-FR";
            case 2 -> "nl-NL";
            default -> SharedVariables.DEFAULT_LOCALE;
        };

        if (!languageToMatch.equals(newLanguage)) {
            FxUtils.showInformationAlert(resourceBundle.getString("RestartRequired"), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Reset the settings
     */
    @FXML
    private void resetSettingsAction() {
        logger.info("Attempting to reset all settings");
        if (FxUtils.showConfirmationAlert(resourceBundle.getString("ConfirmReset"), getClass().getResourceAsStream(SharedVariables.ICON_URL))) {
            showAlertIfLanguageMismatch(SharedVariables.DEFAULT_LOCALE);

            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            try {
                settingsController.createDefaultProperties();
                settingsController.setProperties(settingsController.readPropertiesFile());

                final Properties properties = settingsController.getProperties();

                final int socketTimeout = Integer.parseInt(properties.getProperty("socketTimeout", "2000"));
                int threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize", "-1"));
                if (threadPoolSize == -1) {
                    threadPoolSize = Runtime.getRuntime().availableProcessors();
                }

                portController.setSocketTimeout(socketTimeout);
                portController.setThreadPoolSize(threadPoolSize);

                loadSettings();
            } catch (final IOException ex) {
                logger.error("Unable to reset all settings", ex);
                FxUtils.showErrorAlert(resourceBundle.getString("ResetSettingsError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            }
        }
    }

    /**
     * Cancel changing the settings
     *
     * @param event The {@link ActionEvent} argument
     */
    @FXML
    private void cancelAction(final ActionEvent event) {
        logger.info("Closing SettingsWindow");
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Save the settings
     */
    @FXML
    private void saveSettingsAction() {

    }
}
