package org.acc.service.sdk.data.purchase;

import java.math.BigInteger;

public class SaveNewPurchase {
    public String purchaseId;
    public BigInteger cashAmount;
    public BigInteger loyalty;
    public String currency;
    public String shopId;
    public String userAccount;
    public String userPhoneHash;
    public String sender;
    public String purchaseSignature;

    public SaveNewPurchase(
            String purchaseId,
            BigInteger cashAmount,
            BigInteger loyalty,
            String currency,
            String shopId,
            String userAccount,
            String userPhoneHash,
            String sender,
            String purchaseSignature
    ) {
        this.purchaseId = purchaseId;
        this.cashAmount = cashAmount;
        this.loyalty = loyalty;
        this.currency = currency;
        this.shopId = shopId;
        this.userAccount = userAccount;
        this.userPhoneHash = userPhoneHash;
        this.sender = sender;
        this.purchaseSignature = purchaseSignature;
    }
}
