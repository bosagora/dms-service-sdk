package org.acc.service.sdk.data.payment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Data generated during the payment process
 */
public class PaymentTaskItem {
    public String paymentId;
    public String purchaseId;
    public BigInteger amount;
    public String currency;
    public String shopId;
    public String account;
    public BigInteger paidPoint;
    public BigInteger paidValue;
    public BigInteger feePoint;
    public BigInteger feeValue;
    public BigInteger totalPoint;
    public BigInteger totalValue;
    public String terminalId;
    public int paymentStatus;

    public PaymentTaskItem(
            String paymentId,
            String purchaseId,
            BigInteger amount,
            String currency,
            String shopId,
            String account,
            BigInteger paidPoint,
            BigInteger paidValue,
            BigInteger feePoint,
            BigInteger feeValue,
            BigInteger totalPoint,
            BigInteger totalValue,
            String terminalId,
            int paymentStatus
    ) {
        this.paymentId = paymentId;
        this.purchaseId = purchaseId;
        this.amount = amount;
        this.currency = currency;
        this.shopId = shopId;
        this.account = account;
        this.paidPoint = paidPoint;
        this.paidValue = paidValue;
        this.feePoint = feePoint;
        this.feeValue = feeValue;
        this.totalPoint = totalPoint;
        this.totalValue = totalValue;
        this.terminalId = terminalId;
        this.paymentStatus = paymentStatus;
    }

    @NotNull
    @Contract("_ -> new")
    public static PaymentTaskItem fromJSONObject(JSONObject data) {
        return new PaymentTaskItem(
                data.getString("paymentId"),
                data.getString("purchaseId"),
                new BigInteger(data.getString("amount"), 10),
                data.getString("currency"),
                data.getString("shopId"),
                data.getString("account"),
                new BigInteger(data.getString("paidPoint"), 10),
                new BigInteger(data.getString("paidValue"), 10),
                new BigInteger(data.getString("feePoint"), 10),
                new BigInteger(data.getString("feeValue"), 10),
                new BigInteger(data.getString("totalPoint"), 10),
                new BigInteger(data.getString("totalValue"), 10),
                data.getString("terminalId"),
                data.getInt("paymentStatus")
        );
    }

    public PaymentTaskItem cloneTaskItem() {
        return new PaymentTaskItem(
                this.paymentId,
                this.purchaseId,
                this.amount,
                this.currency,
                this.shopId,
                this.account,
                this.paidPoint,
                this.paidValue,
                this.feePoint,
                this.feeValue,
                this.totalPoint,
                this.totalValue,
                this.terminalId,
                this.paymentStatus
        );
    }
}
