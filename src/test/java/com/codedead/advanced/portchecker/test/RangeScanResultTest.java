package com.codedead.advanced.portchecker.test;

import com.codedead.advanced.portchecker.domain.RangeScanResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeScanResultTest {

    @Test
    void settingTheHostReturnsTheHost() {
        final RangeScanResult rangeScanResult = new RangeScanResult("localhost");
        assertEquals("localhost", rangeScanResult.getHost());
    }

    @Test
    void addingAPortReturnsThePort() {
        final RangeScanResult rangeScanResult = new RangeScanResult("localhost");
        rangeScanResult.addPort(80, true);
        assertEquals(1, rangeScanResult.getPorts().size());
    }

    @Test
    void addingAPortReturnsTheOpenStatus() {
        final RangeScanResult rangeScanResult = new RangeScanResult("localhost");
        rangeScanResult.addPort(80, true);
        assertTrue(rangeScanResult.getPorts().get(80));
    }
}
