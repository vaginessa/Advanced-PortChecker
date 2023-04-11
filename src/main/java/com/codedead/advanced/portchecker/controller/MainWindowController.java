package com.codedead.advanced.portchecker.controller;

import com.codedead.advanced.portchecker.interfaces.IRunnableHelper;
import com.codedead.advanced.portchecker.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindowController {

    private PortController portController;
    private SettingsController settingsController;
    private ResourceBundle translationBundle;
    private final HelpUtils helpUtils;
    private final Logger logger;

    /**
     * Initialize a new MainWindowController
     */
    public MainWindowController() {
        this.logger = LogManager.getLogger(MainWindowController.class);
        this.helpUtils = new HelpUtils();

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

    /**
     * Set the {@link ResourceBundle} object
     *
     * @param translationBundle The {@link ResourceBundle} object
     */
    public void setResourceBundle(final ResourceBundle translationBundle) {
        if (translationBundle == null)
            throw new NullPointerException("ResourceBundle cannot be null");

        this.translationBundle = translationBundle;
    }

    /**
     * Exit the application
     */
    @FXML
    private void exitApplication() {
        logger.info("Exiting application");
        System.exit(0);
    }

    /**
     * Open the help document
     */
    @FXML
    private void helpAction() {
        logger.info("Attempting to open the help file");

        try {
            helpUtils.openFileFromResources(new RunnableFileOpener(SharedVariables.HELP_DOCUMENTATION_FILE_LOCATION, new IRunnableHelper() {
                @Override
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the help file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the help file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }), SharedVariables.HELP_DOCUMENTATION_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Error opening the help file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("HelpFileError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Open the CodeDead website
     */
    @FXML
    private void homepageAction() {
        helpUtils.openCodeDeadWebSite(translationBundle);
    }

    /**
     * Open the license file
     */
    @FXML
    private void licenseAction() {
        helpUtils.openLicenseFile(translationBundle);
    }

    /**
     * Method that is called when the donation website should be opened
     */
    @FXML
    private void donateAction() {
        logger.info("Opening the CodeDead donation website");

        final RunnableSiteOpener runnableSiteOpener = new RunnableSiteOpener("https://codedead.com/donate", new IRunnableHelper() {
            @Override
            public void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        logger.error("Error opening the CodeDead donation website", ex);
                        FxUtils.showErrorAlert(translationBundle.getString("WebsiteError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                    }
                });
            }
        });

        new Thread(runnableSiteOpener).start();
    }

    /**
     * Method that is called when the AboutWindow should be opened
     */
    @FXML
    private void aboutAction() {
        logger.info("Attempting to open the AboutWindow");

        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/AboutWindow.fxml"), translationBundle);
            final Parent root = loader.load();

            final AboutWindowController aboutWindowController = loader.getController();
            aboutWindowController.setResourceBundle(translationBundle);

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("AboutWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/AdvancedPortChecker.png"))));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the AboutWindow");
            primaryStage.show();
        } catch (final IOException ex) {
            logger.error("Unable to open the AboutWindow", ex);
            FxUtils.showErrorAlert(translationBundle.getString("AboutWindowError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    @FXML
    private void updateAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    private void settingsAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
