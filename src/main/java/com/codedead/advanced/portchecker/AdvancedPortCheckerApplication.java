package com.codedead.advanced.portchecker;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.codedead.advanced.portchecker.controller.MainWindowController;
import com.codedead.advanced.portchecker.controller.PortController;
import com.codedead.advanced.portchecker.controller.SettingsController;
import com.codedead.advanced.portchecker.controller.UpdateController;
import com.codedead.advanced.portchecker.utils.FxUtils;
import com.codedead.advanced.portchecker.utils.SharedVariables;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.codedead.advanced.portchecker.utils.SharedVariables.DEFAULT_LOCALE;

public class AdvancedPortCheckerApplication extends Application {

    private static Logger logger;

    /**
     * Initialize the application
     *
     * @param args The application arguments
     */
    public static void main(final String[] args) {
        System.setProperty("logBasePath", SharedVariables.PROPERTIES_BASE_PATH);
        logger = LogManager.getLogger(AdvancedPortCheckerApplication.class);

        Level logLevel = Level.ERROR;
        try (final FileInputStream fis = new FileInputStream(SharedVariables.PROPERTIES_FILE_LOCATION)) {
            final Properties prop = new Properties();
            prop.load(fis);

            logLevel = switch (prop.getProperty("logLevel", "ERROR")) {
                case "OFF" -> Level.OFF;
                case "FATAL" -> Level.FATAL;
                case "WARN" -> Level.WARN;
                case "DEBUG" -> Level.DEBUG;
                case "TRACE" -> Level.TRACE;
                case "INFO" -> Level.INFO;
                case "ALL" -> Level.ALL;
                default -> Level.ERROR;
            };
        } catch (final IOException ex) {
            logger.error("Properties object could not be loaded", ex);
        }
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), logLevel);
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        Platform.setImplicitExit(false);
        final SettingsController settingsController;

        try {
            settingsController = new SettingsController(SharedVariables.PROPERTIES_FILE_LOCATION, SharedVariables.PROPERTIES_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Unable to initialize the SettingsController", ex);
            FxUtils.showErrorAlert("Exception occurred", ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
            Platform.exit();
            return;
        }

        final Properties properties = settingsController.getProperties();
        final String languageTag = properties.getProperty("locale", DEFAULT_LOCALE);

        final String theme = properties.getProperty("theme", "Light");
        switch (theme.toLowerCase()) {
            case "nordlight" -> Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
            case "norddark" -> Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
            case "dark" -> Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            default -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }

        final Locale locale = Locale.forLanguageTag(languageTag);
        final ResourceBundle translationBundle = ResourceBundle.getBundle("translations.AdvancedPortChecker", locale);

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/windows/MainWindow.fxml"), translationBundle);
        Parent root;
        try {
            root = loader.load();
        } catch (final IOException ex) {
            logger.error("Unable to load FXML for MainWindow", ex);
            Platform.exit();
            return;
        }

        logger.info("Creating the MainWindowController");

        final int socketTimeout = Integer.parseInt(properties.getProperty("socketTimeout", "2000"));
        int threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize", "-1"));
        if (threadPoolSize == -1) {
            threadPoolSize = Runtime.getRuntime().availableProcessors();
        }

        final MainWindowController mainWindowController = loader.getController();
        mainWindowController.setResourceBundle(translationBundle);
        mainWindowController.setPortController(new PortController(socketTimeout, threadPoolSize));
        mainWindowController.setSettingsController(settingsController);
        mainWindowController.setUpdateController(new UpdateController(properties.getProperty("updateApi", "https://codedead.com/Software/Advanced%20PortChecker/version.json"), SharedVariables.CURRENT_VERSION));

        final Scene scene = new Scene(root);

        primaryStage.setTitle(translationBundle.getString("MainWindowTitle"));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(SharedVariables.ICON_URL))));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        logger.info("Showing the MainWindow");
        primaryStage.show();
    }
}
