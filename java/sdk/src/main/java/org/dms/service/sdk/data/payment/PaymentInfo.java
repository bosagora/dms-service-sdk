package org.dms.service.sdk.data.payment;

import java.math.BigInteger;

public class PaymentInfo {
    public String account;
    public BigInteger amount;
    public String currency;
    public BigInteger balance;
    public BigInteger balanceValue;
    public BigInteger paidPoint;
    public BigInteger paidValue;
    public BigInteger feePoint;
    public BigInteger feeValue;
    public BigInteger totalPoint;
    public BigInteger totalValue;

    public PaymentInfo(String account, BigInteger amount,
                       String currency,
                       BigInteger balance, BigInteger balanceValue,
                       BigInteger paidPoint, BigInteger paidValue,
                       BigInteger feePoint, BigInteger feeValue,
                       BigInteger totalPoint, BigInteger totalValue) {
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.balance = balance;
        this.balanceValue = balanceValue;
        this.paidPoint = paidPoint;
        this.paidValue = paidValue;
        this.feePoint = feePoint;
        this.feeValue = feeValue;
        this.totalPoint = totalPoint;
        this.totalValue = totalValue;
    }
}
