package com.codedead.advanced.portchecker.domain;

public final class InvalidHttpResponseCodeException extends Exception {

    /**
     * Initialize a new InvalidHttpResponseCodeException
     *
     * @param message The error message
     */
    public InvalidHttpResponseCodeException(final String message) {
        super(message);
    }
}
