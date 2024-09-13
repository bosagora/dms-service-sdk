package org.dms.service.sdk.data;

import java.math.BigInteger;

/**
 * Data with balance saved
 */
public class Balance {
    /**
     * Balance for a particular currency
     */
    public BigInteger balance;
    /**
     * Balance for a default currency(Point)
     */
    public BigInteger value;

    /**
     * Constructor
     * @param balance Balance for a particular currency
     * @param value Balance for a default currency(Point)
     */
    public Balance(BigInteger balance, BigInteger value) {
        this.balance = balance;
        this.value = value;
    }
}
