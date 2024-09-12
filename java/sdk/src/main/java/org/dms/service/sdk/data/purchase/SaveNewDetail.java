package org.dms.service.sdk.data.purchase;

import java.math.BigInteger;

public class SaveNewDetail {
    public String productId;
    public BigInteger amount;
    public BigInteger providePercent;

    public SaveNewDetail(String productId, BigInteger amount, BigInteger providePercent) {
        this.productId = productId;
        this.amount = amount;
        this.providePercent = providePercent;
    }
}
