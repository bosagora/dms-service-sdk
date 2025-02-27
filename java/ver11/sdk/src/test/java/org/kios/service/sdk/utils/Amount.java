package org.kios.service.sdk.utils;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmountTest {
    @Test
    void make() {
        try {
            Amount amount = Amount.make("1");
            assertEquals(amount.getValue(), new BigInteger("1000000000000000000", 10));
            assertEquals(amount.toAmountString(), "1.000000000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1.45");
            assertEquals(amount.getValue(), new BigInteger("1450000000000000000", 10));
            assertEquals(amount.toAmountString(), "1.450000000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.5678");
            assertEquals(amount.getValue(), new BigInteger("1234567800000000000000", 10));
            assertEquals(amount.toAmountString(), "1234.567800000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.05678");
            assertEquals(amount.getValue(), new BigInteger("1234056780000000000000", 10));
            assertEquals(amount.toAmountString(), "1234.056780000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.0000000000000005678");
            assertEquals(amount.getValue(), new BigInteger("1234000000000000000567", 10));
            assertEquals(amount.toAmountString(), "1234.000000000000000567");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.0000000000000000005678");
            assertEquals(amount.getValue(), new BigInteger("1234000000000000000000", 10));
            assertEquals(amount.toAmountString(), "1234.000000000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1_234.5678");
            assertEquals(amount.getValue(), new BigInteger("1234567800000000000000", 10));
            assertEquals(amount.toAmountString(), "1234.567800000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1,234.5678");
            assertEquals(amount.getValue(), new BigInteger("1234567800000000000000", 10));
            assertEquals(amount.toAmountString(), "1234.567800000000000000");
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }
    }
}
