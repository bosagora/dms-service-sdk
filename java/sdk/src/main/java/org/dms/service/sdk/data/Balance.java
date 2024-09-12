package org.dms.service.sdk.data;

import java.math.BigInteger;

public class Balance {
    public BigInteger balance;
    public BigInteger value;

    public Balance(BigInteger balance, BigInteger value) {
        this.balance = balance;
        this.value = value;
    }
}
