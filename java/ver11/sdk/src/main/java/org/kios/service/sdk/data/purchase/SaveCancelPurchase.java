package org.kios.service.sdk.data.purchase;

public class SaveCancelPurchase {
    public String purchaseId;
    public String sender;
    public String purchaseSignature;

    public SaveCancelPurchase(
            String purchaseId,
            String sender,
            String purchaseSignature
    ) {
        this.purchaseId = purchaseId;
        this.sender = sender;
        this.purchaseSignature = purchaseSignature;
    }
}
