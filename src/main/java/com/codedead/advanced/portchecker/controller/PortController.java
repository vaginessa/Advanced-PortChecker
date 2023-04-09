package com.codedead.advanced.portchecker.controller;

import com.codedead.advanced.portchecker.domain.PortScanResult;
import com.codedead.advanced.portchecker.domain.RangeScanResult;
import inet.ipaddr.HostName;
import inet.ipaddr.HostNameException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class PortController {

    private int socketTimeout;
    private int threadPoolSize;
    private ExecutorService threadPool;
    private final Logger logger;

    /**
     * Initialize a new PortController with a default socket timeout of 2000 milliseconds
     */
    public PortController() {
        this(2000);
    }

    /**
     * Initialize a new PortController
     *
     * @param socketTimeout The socket timeout
     */
    public PortController(final int socketTimeout) {
        this(socketTimeout, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Initialize a new PortController
     *
     * @param socketTimeout  The socket timeout
     * @param threadPoolSize The thread pool size
     */
    public PortController(final int socketTimeout, final int threadPoolSize) {
        setSocketTimeout(socketTimeout);
        setThreadPoolSize(threadPoolSize);
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        logger = LogManager.getLogger(PortController.class);
    }

    /**
     * Get the socket timeout
     *
     * @return The socket timeout
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Set the socket timeout
     *
     * @param socketTimeout The socket timeout
     */
    public void setSocketTimeout(final int socketTimeout) {
        if (socketTimeout <= 0)
            throw new IllegalArgumentException("Socket timeout cannot be less than 1");

        this.socketTimeout = socketTimeout;
    }

    /**
     * Get the thread pool size
     *
     * @return The thread pool size
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Set the thread pool size
     *
     * @param threadPoolSize The thread pool size
     */
    public void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize <= 0)
            throw new IllegalArgumentException("Thread pool size cannot be less than 1");

        this.threadPoolSize = threadPoolSize;
    }

    /**
     * Check if a port number is valid
     *
     * @param portNumber The port number to check
     * @return True if the port number is valid, false otherwise
     */
    public boolean isValidPortNumber(final int portNumber) throws NumberFormatException {
        logger.info("Checking if port {} is valid", portNumber);
        return portNumber >= 0 && portNumber <= 65535;
    }

    public boolean isValidHost(final String hostStr) {
        logger.info("Checking if host {} is valid", hostStr);

        final HostName host = new HostName(hostStr);
        try {
            host.validate();
            return true;
        } catch (final HostNameException e) {
            return false;
        }
    }

    /**
     * Check if a port is open on a specific host
     *
     * @param host       The host to check
     * @param portNumber The port number to check
     * @return True if the port is open, false otherwise
     * @throws IllegalArgumentException If the port number is not valid
     */
    public boolean isPortOpen(final String host, final int portNumber) throws IllegalArgumentException {
        logger.info("Checking if port {} is open", portNumber);

        if (!isValidHost(host))
            throw new IllegalArgumentException("Host is not valid");
        if (!isValidPortNumber(portNumber))
            throw new IllegalArgumentException("Port number is not valid");

        try (final Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, portNumber), socketTimeout);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    public CompletableFuture<PortScanResult> isPortOpenAsync(final String host, final int portNumber) {
        logger.info("Checking if port {} is open asynchronously", portNumber);

        if (!isValidHost(host))
            throw new IllegalArgumentException("Host is not valid");
        if (!isValidPortNumber(portNumber))
            throw new IllegalArgumentException("Port number is not valid");

        return CompletableFuture.supplyAsync(() -> {
            try (final Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, portNumber), socketTimeout);
                return new PortScanResult(host, portNumber, true);
            } catch (final IOException e) {
                return new PortScanResult(host, portNumber, false);
            }
        }, threadPool);
    }

    /**
     * Scan a range of ports on a specific host
     *
     * @param host      The host to scan
     * @param startPort The start port
     * @param endPort   The end port
     * @return A {@link RangeScanResult} containing the results of the scan
     */
    public RangeScanResult scanRange(final String host, final int startPort, final int endPort) {
        logger.info("Scanning range {}-{} on host {}", startPort, endPort, host);

        if (!isValidHost(host))
            throw new IllegalArgumentException("Host is not valid");
        if (!isValidPortNumber(startPort))
            throw new IllegalArgumentException("Start port is not valid");
        if (!isValidPortNumber(endPort))
            throw new IllegalArgumentException("End port is not valid");

        final RangeScanResult rangeScanResult = new RangeScanResult(host);
        for (int i = startPort; i <= endPort; i++) {
            rangeScanResult.addPort(i, isPortOpen(host, i));
        }

        return rangeScanResult;
    }

    /**
     * Scan a range of ports on a specific host asynchronously
     *
     * @param host      The host to scan
     * @param startPort The start port
     * @param endPort   The end port
     * @return A {@link List} of {@link CompletableFuture} objects containing a {@link PortScanResult} object
     */
    public List<CompletableFuture<PortScanResult>> scanRangeAsync(final String host, final int startPort, final int endPort) {
        logger.info("Scanning range {}-{} on host {} asynchronously", startPort, endPort, host);

        if (!isValidHost(host))
            throw new IllegalArgumentException("Host is not valid");
        if (!isValidPortNumber(startPort))
            throw new IllegalArgumentException("Start port is not valid");
        if (!isValidPortNumber(endPort))
            throw new IllegalArgumentException("End port is not valid");

        final List<CompletableFuture<PortScanResult>> portOpenFutures = new ArrayList<>();

        for (int i = startPort; i <= endPort; i++) {
            portOpenFutures.add(isPortOpenAsync(host, i));
        }

        return portOpenFutures;
    }

    /**
     * Shut down the thread pool immediately
     */
    public void shutdownNow() {
        logger.info("Shutting down thread pool");

        threadPool.shutdownNow();
        //noinspection StatementWithEmptyBody
        while (!threadPool.isTerminated()) {
            // Wait for the thread pool to terminate
        }

        threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }
}
