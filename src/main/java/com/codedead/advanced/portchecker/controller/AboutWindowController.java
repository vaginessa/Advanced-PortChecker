package com.codedead.advanced.portchecker.controller;

import com.codedead.advanced.portchecker.utils.HelpUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

public class AboutWindowController {

    private ResourceBundle translationBundle;
    private final HelpUtils helpUtils;
    private final Logger logger;

    /**
     * Initialize a new AboutWindowController
     */
    public AboutWindowController() {
        logger = LogManager.getLogger(AboutWindowController.class);
        helpUtils = new HelpUtils();

        logger.info("Initializing new AboutWindowController object");
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
     * Open the CodeDead website
     */
    @FXML
    private void codeDeadAction() {
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
     * Close thw window
     *
     * @param actionEvent The {@link ActionEvent} object
     */
    @FXML
    public void closeAction(final ActionEvent actionEvent) {
        logger.info("Closing AboutWindow");
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
