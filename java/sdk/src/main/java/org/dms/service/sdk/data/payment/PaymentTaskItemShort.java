package org.dms.service.sdk.data.payment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Reduction of data generated during the payment process
 */
public class PaymentTaskItemShort {
    public String paymentId;
    public String purchaseId;
    public BigInteger amount;
    public String currency;
    public String shopId;
    public String account;
    public String terminalId;
    public int paymentStatus;

    public PaymentTaskItemShort(
            String paymentId,
            String purchaseId,
            BigInteger amount,
            String currency,
            String shopId,
            String account,
            String terminalId,
            int paymentStatus
    ) {
        this.paymentId = paymentId;
        this.purchaseId = purchaseId;
        this.amount = amount;
        this.currency = currency;
        this.shopId = shopId;
        this.account = account;
        this.terminalId = terminalId;
        this.paymentStatus = paymentStatus;
    }
    @NotNull
    @Contract("_ -> new")
    public static PaymentTaskItemShort fromJSONObject(JSONObject data) {
        return new PaymentTaskItemShort(
                data.getString("paymentId"),
                data.getString("purchaseId"),
                new BigInteger(data.getString("amount"), 10),
                data.getString("currency"),
                data.getString("shopId"),
                data.getString("account"),
                data.has("terminalId") ? data.getString("terminalId") : "",
                data.getInt("paymentStatus")
        );
    }

    public PaymentTaskItemShort cloneTaskItem() {
        return new PaymentTaskItemShort(
                this.paymentId,
                this.purchaseId,
                this.amount,
                this.currency,
                this.shopId,
                this.account,
                this.terminalId,
                this.paymentStatus
        );
    }
}
