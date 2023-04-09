package com.codedead.advanced.portchecker.domain;

import java.util.HashMap;
import java.util.Map;

public final class RangeScanResult {

    private final String host;
    private final Map<Integer, Boolean> ports;

    /**
     * Initialize a new RangeScanResult
     *
     * @param host The host
     */
    public RangeScanResult(final String host) {
        this.host = host;
        ports = new HashMap<>();
    }

    /**
     * Add a port to the result
     *
     * @param portNumber The port number
     * @param isOpen     True if the port is open, false otherwise
     */
    public void addPort(final int portNumber, final boolean isOpen) {
        ports.put(portNumber, isOpen);
    }

    /**
     * Get the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the ports
     *
     * @return A {@link Map} containing the port number and a boolean indicating if the port is open
     */
    public Map<Integer, Boolean> getPorts() {
        return ports;
    }
}
