package com.codedead.advanced.portchecker.test;

import com.codedead.advanced.portchecker.controller.PortController;
import com.codedead.advanced.portchecker.domain.PortScanResult;
import com.codedead.advanced.portchecker.domain.RangeScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class PortControllerTest {

    private PortController portController;

    @BeforeEach
    void setup() {
        portController = new PortController();
    }

    @Test
    void getSocketTimeOutReturnsCorrectValue() {
        portController.setSocketTimeout(2000);
        assertEquals(2000, portController.getSocketTimeout());
    }

    @Test
    void setThreadPoolSizeToZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portController.setThreadPoolSize(0));
    }

    @Test
    void negativeThreadPoolSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portController.setThreadPoolSize(-1));
    }

    @Test
    void validThreadPoolSizeDoesNotThrowException() {
        assertDoesNotThrow(() -> portController.setThreadPoolSize(1));
    }

    @Test
    void settingThreadPoolSizeToValidValueSetsThreadPoolSize() {
        portController.setThreadPoolSize(1);
        assertEquals(1, portController.getThreadPoolSize());
    }

    @Test
    void negativeSocketTimeoutIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> portController.setSocketTimeout(-1));
    }

    @Test
    void zeroSocketTimeoutIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> portController.setSocketTimeout(0));
    }

    @Test
    void negativePortNumberIsInvalid() {
        assertFalse(portController.isValidPortNumber(-1));
    }

    @Test
    void zeroPortNumberIsValid() {
        assertTrue(portController.isValidPortNumber(0));
    }

    @Test
    void portNumber65535IsValid() {
        assertTrue(portController.isValidPortNumber(65535));
    }

    @Test
    void portNumber65536IsInvalid() {
        assertFalse(portController.isValidPortNumber(65536));
    }

    @Test
    void portNumber100000IsInvalid() {
        assertFalse(portController.isValidPortNumber(100000));
    }

    @Test
    void invalidHostNameReturnsFalse() {
        assertFalse(portController.isValidHost("invalid host"));
    }

    @Test
    void validHostNameReturnsTrue() {
        assertTrue(portController.isValidHost("localhost"));
    }

    @Test
    void invalidPortNumberThrowsExceptionWhenCheckingIfPortIsOpen() {
        assertThrows(IllegalArgumentException.class, () -> portController.isPortOpen("localhost", -1));
    }

    @Test
    void validPortNumberDoesNotThrowExceptionWhenCheckingIfPortIsOpen() {
        assertDoesNotThrow(() -> portController.isPortOpen("localhost", 0));
    }

    @Test
    void invalidHostNameThrowsExceptionWhenCheckingIfPortIsOpen() {
        assertThrows(IllegalArgumentException.class, () -> portController.isPortOpen("invalid host", 0));
    }

    @Test
    void validPortNumberDoesNotThrowExceptionWhenCheckingIfPortIsOpenAsync() {
        assertDoesNotThrow(() -> portController.isPortOpenAsync("localhost", 0));
    }

    @Test
    void invalidPortNumberThrowsExceptionWhenCheckingIfPortIsOpenAsync() {
        assertThrows(IllegalArgumentException.class, () -> portController.isPortOpenAsync("localhost", -1));
    }

    @Test
    void invalidHostNameThrowsExceptionWhenCheckingIfPortIsOpenAsync() {
        assertThrows(IllegalArgumentException.class, () -> portController.isPortOpenAsync("invalid host", 0));
    }

    @Test
    void asyncRangeScanReturnsValidResult() throws ExecutionException, InterruptedException {
        final List<CompletableFuture<PortScanResult>> res = portController.scanRangeAsync("localhost", 0, 99);
        final CompletableFuture<List<PortScanResult>> futureOfList = CompletableFuture
                .allOf(res.toArray(new CompletableFuture[0]))
                .thenApply(v -> res.stream().map(CompletableFuture::join).collect(toList()));

        final List<PortScanResult> results = futureOfList.get();
        assertEquals(100, results.size());
    }

    @Test
    void asyncRangeScanThrowsIllegalArgumentExceptionWithInvalidHostName() {
        assertThrows(IllegalArgumentException.class, () -> portController.scanRangeAsync("invalid host", 0, 99));
        assertThrows(IllegalArgumentException.class, () -> portController.scanRangeAsync("https://codedead.com", 0, 99));
    }

    @Test
    void asyncRangeScanThrowsIllegalArgumentExceptionWithInvalidPortNumbers() {
        assertThrows(IllegalArgumentException.class, () -> portController.scanRangeAsync("codedead.com", -1, 99));
        assertThrows(IllegalArgumentException.class, () -> portController.scanRangeAsync("codedead.com", 0, 65536));
    }

    @Test
    void rangeScanReturnsValidResult() {
        final RangeScanResult result = portController.scanRange("localhost", 0, 99);
        assertEquals(100, result.getPorts().size());
    }

    @Test
    void rangeScanThrowsIllegalArgumentExceptionWithInvalidHostName() {
        assertThrows(IllegalArgumentException.class, () -> portController.scanRange("invalid host", 0, 99));
        assertThrows(IllegalArgumentException.class, () -> portController.scanRange("https://codedead.com", 0, 99));
    }

    @Test
    void rangeScanThrowsIllegalArgumentExceptionWithInvalidPortNumbers() {
        assertThrows(IllegalArgumentException.class, () -> portController.scanRange("localhost", -1, 99));
        assertThrows(IllegalArgumentException.class, () -> portController.scanRange("localhost", 0, 65536));
    }

    @Test
    void shuttingDownExecutorServiceDoesNotThrowException() {
        assertDoesNotThrow(() -> portController.shutdownNow());
    }

    @Test
    void scanningRangeAsyncAfterShutdownDoesNotThrowException() {
        assertDoesNotThrow(() -> portController.scanRangeAsync("localhost", 0, 5000));
        portController.shutdownNow();
        assertDoesNotThrow(() -> portController.scanRangeAsync("localhost", 0, 99));
    }

    @Test
    void checkingIfPortIsOpebAsyncAfterShutdownDoesNotThrowException() {
        assertDoesNotThrow(() -> portController.isPortOpenAsync("localhost", 0));
        portController.shutdownNow();
        assertDoesNotThrow(() -> portController.isPortOpenAsync("localhost", 0));
    }

    @Test
    void codeDeadPort80IsOpen() {
        assertTrue(portController.isPortOpen("codedead.com", 80));
    }

    @Test
    void codeDeadPort80IsOpenAsync() throws ExecutionException, InterruptedException {
        final CompletableFuture<PortScanResult> future = portController.isPortOpenAsync("codedead.com", 80);
        assertTrue(future.get().isOpen());
    }
}
