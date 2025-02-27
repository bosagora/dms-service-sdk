package org.kios.service.sdk.data.purchase;

public class PurchaseDetail {
    public String productId;
    public String amount;
    public long providePercent;

    public PurchaseDetail(String productId, String amount, long providePercent) {
        this.productId = productId;
        this.amount = amount;
        this.providePercent = providePercent;
    }
}
