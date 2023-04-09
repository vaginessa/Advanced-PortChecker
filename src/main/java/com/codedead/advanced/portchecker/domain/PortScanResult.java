package com.codedead.advanced.portchecker.domain;

public final class PortScanResult {

    private final String host;
    private final int port;
    private final boolean isOpen;

    /**
     * Initialize a new PortScanResult
     *
     * @param host   The host
     * @param port   The port number
     * @param isOpen True if the port is open, false otherwise
     */
    public PortScanResult(final String host, final int port, final boolean isOpen) {
        this.host = host;
        this.port = port;
        this.isOpen = isOpen;
    }

    /**
     * Get the host
     *
     * @return The host
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port number
     *
     * @return The port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the open status
     *
     * @return True if the port is open, false otherwise
     */
    public boolean isOpen() {
        return isOpen;
    }
}
