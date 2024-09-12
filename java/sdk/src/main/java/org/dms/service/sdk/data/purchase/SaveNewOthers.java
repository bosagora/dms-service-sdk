package org.dms.service.sdk.data.purchase;

import java.math.BigInteger;

public class SaveNewOthers {
    public BigInteger totalAmount;
    public long timestamp;
    public long waiting;

    public SaveNewOthers(BigInteger totalAmount, long timestamp, long waiting) {
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
        this.waiting = waiting;
    }
}
