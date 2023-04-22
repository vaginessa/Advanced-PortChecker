package com.codedead.advanced.portchecker.domain;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public final class NumberTextField extends TextField {

    @FXML
    private int min;

    /**
     * Initialize a new NumberTextField
     */
    public NumberTextField() {
        this.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                int currentValue = 0;
                if (!this.getText().isEmpty()) {
                    currentValue = Integer.parseInt(this.getText());
                }
                currentValue++;
                this.setText(Integer.toString(currentValue));
            } else if (e.getCode() == KeyCode.DOWN) {
                int currentValue = 0;
                if (!this.getText().isEmpty()) {
                    currentValue = Integer.parseInt(this.getText());
                }
                if (currentValue - 1 >= getMin()) {
                    currentValue--;
                    this.setText(Integer.toString(currentValue));
                }
            }
        });
    }

    /**
     * Method that is invoked when a {@link String} object is replaced
     *
     * @param start The start index of the replacement
     * @param end   The end index of the replacement
     * @param text  The new {@link String} object
     */
    @Override
    public void replaceText(final int start, final int end, final String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    /**
     * Method that is invoked when a {@link String} object is being replaced
     *
     * @param text The new {@link String} object
     */
    @Override
    public void replaceSelection(final String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    /**
     * Validate whether the text is a number or not
     *
     * @param text The text that should be validated
     * @return True if the text is a number, otherwise false
     */
    private boolean validate(final String text) {
        return text.matches("\\d*");
    }

    /**
     * Get the minimum allowed value
     *
     * @return The minimum allowed value
     */
    @FXML
    public int getMin() {
        return min;
    }

    /**
     * Set the minimum allowed value
     *
     * @param min The minimum allowed value
     */
    @FXML
    public void setMin(final int min) {
        this.min = min;
    }
}
