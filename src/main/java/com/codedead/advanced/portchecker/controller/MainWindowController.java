package com.codedead.advanced.portchecker.controller;

import com.codedead.advanced.portchecker.domain.InvalidHttpResponseCodeException;
import com.codedead.advanced.portchecker.domain.OsCheck;
import com.codedead.advanced.portchecker.domain.PlatformUpdate;
import com.codedead.advanced.portchecker.interfaces.IRunnableHelper;
import com.codedead.advanced.portchecker.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainWindowController {

    private PortController portController;
    private SettingsController settingsController;
    private UpdateController updateController;
    private ResourceBundle translationBundle;
    private final String platformName;
    private final HelpUtils helpUtils;
    private final Logger logger;

    /**
     * Initialize a new MainWindowController
     */
    public MainWindowController() {
        this.platformName = OsCheck.getOperatingSystemType().name();

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
     * Get the {@link UpdateController} object
     *
     * @return The {@link UpdateController} object
     */
    public UpdateController getUpdateController() {
        return updateController;
    }

    /**
     * Set the {@link UpdateController} object
     *
     * @param updateController The {@link UpdateController} object
     */
    public void setUpdateController(final UpdateController updateController) {
        if (updateController == null)
            throw new NullPointerException("UpdateController cannot be null!");

        this.updateController = updateController;

        final Properties properties = settingsController.getProperties();
        final boolean shouldUpdate = Boolean.parseBoolean(properties.getProperty("autoUpdate", "true"));

        if (shouldUpdate) {
            checkForUpdates(false, false);
        }
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
     * Check for application updates
     *
     * @param showNoUpdates  Show an {@link Alert} object when no updates are available
     * @param showExceptions Show an {@link Alert} object when an exception occurs
     */
    private void checkForUpdates(final boolean showNoUpdates, final boolean showExceptions) {
        logger.info("Attempting to check for updates");

        try {
            final Optional<PlatformUpdate> platformUpdate = updateController.checkForUpdates(platformName, SharedVariables.PORTABLE);
            if (platformUpdate.isPresent()) {
                final PlatformUpdate update = platformUpdate.get();

                logger.info("Version {}.{}.{}.{} is available", update.getMajorVersion(), update.getMinorVersion(), update.getBuildVersion(), update.getRevisionVersion());

                if (FxUtils.showConfirmationAlert(translationBundle
                                .getString("NewUpdateAvailable")
                                .replace("{v}", String.format("%1$s.%2$s.%3$s.%4$s", update.getMajorVersion(), update.getMinorVersion(), update.getBuildVersion(), update.getRevisionVersion())),
                        getClass().getResourceAsStream(SharedVariables.ICON_URL))) {

                    final String extension = update.getDownloadUrl().substring(update.getDownloadUrl().lastIndexOf('.')).toLowerCase();
                    final FileChooser fileChooser = new FileChooser();
                    final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                            String.format("%1$s (*%2$s)", extension.substring(1).toUpperCase(), extension),
                            String.format("*%s", extension));

                    fileChooser.getExtensionFilters().add(extFilter);

                    final File file = fileChooser.showSaveDialog(new Stage());

                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        if (!filePath.toLowerCase().contains(extension)) {
                            filePath += extension;
                        }

                        updateController.downloadFile(update.getDownloadUrl(), filePath);
                        openFile(filePath);
                    }
                }
            } else {
                logger.info("No updates available");
                if (showNoUpdates) {
                    FxUtils.showInformationAlert(translationBundle.getString("NoUpdateAvailable"), null);
                }
            }
        } catch (final InterruptedException ex) {
            logger.error("Unable to check for updates", ex);

            if (showExceptions)
                FxUtils.showErrorAlert(translationBundle.getString("UpdateError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));

            Thread.currentThread().interrupt();
        } catch (final IOException | InvalidHttpResponseCodeException | URISyntaxException ex) {
            logger.error("Unable to check for updates", ex);

            if (showExceptions)
                FxUtils.showErrorAlert(translationBundle.getString("UpdateError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Open a file on the local filesystem
     *
     * @param path The path of the file that should be opened
     */
    private void openFile(final String path) {
        if (path == null)
            throw new NullPointerException("Path cannot be null!");
        if (path.isEmpty())
            throw new IllegalArgumentException("Path cannot be empty!");

        try {
            helpUtils.openFile(new RunnableFileOpener(path, new IRunnableHelper() {
                @Override
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("FileExecutionError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });
                }
            }));
        } catch (final IOException ex) {
            logger.error("Error opening the file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("FileExecutionError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    @FXML
    private void initialize() {

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

    /**
     * Manually check for updates
     */
    @FXML
    private void updateAction() {
        checkForUpdates(true, true);
    }

    /**
     * Open the SettingsWindow
     */
    @FXML
    private void settingsAction() {
        logger.info("Attempting to open the SettingsWindow");
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/SettingsWindow.fxml"), translationBundle);
            final Parent root = loader.load();

            final SettingsWindowController settingsWindowController = loader.getController();
            settingsWindowController.setResourceBundle(translationBundle);

            final Stage primaryStage = new Stage();

            primaryStage.setTitle(translationBundle.getString("SettingsWindowTitle"));
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/AdvancedPortChecker.png"))));
            primaryStage.setScene(new Scene(root));

            logger.info("Showing the SettingsWindow");
            primaryStage.show();
        } catch (final IOException ex) {
            logger.error("Unable to open the SettingsWindow", ex);
            FxUtils.showErrorAlert(translationBundle.getString("SettingsWindowError"), ex.getMessage(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }
}
