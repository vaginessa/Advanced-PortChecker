package com.codedead.advanced.portchecker.test;

import com.codedead.advanced.portchecker.domain.PortScanResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortScanResultTest {

    @Test
    void settingTheHostReturnsTheHost() {
        final PortScanResult portScanResult = new PortScanResult("localhost", 80, true);
        assertEquals("localhost", portScanResult.getHost());
    }

    @Test
    void settingThePortReturnsThePort() {
        final PortScanResult portScanResult = new PortScanResult("localhost", 80, true);
        assertEquals(80, portScanResult.getPort());
    }

    @Test
    void settingTheOpenStatusReturnsTheOpenStatus() {
        final PortScanResult portScanResult = new PortScanResult("localhost", 80, true);
        assertTrue(portScanResult.isOpen());
    }
}
