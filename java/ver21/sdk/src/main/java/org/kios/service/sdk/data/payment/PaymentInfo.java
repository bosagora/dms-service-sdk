package org.kios.service.sdk.data.payment;

import java.math.BigInteger;

/**
 * Response data for the estimated payment amount of the product
 */
public class PaymentInfo {
    /**
     * Wallet address
     */
    public String account;
    /**
     * Purchase amount
     */
    public BigInteger amount;
    /**
     * Currency symbol
     */
    public String currency;
    /**
     * Balance of point
     */
    public BigInteger balance;
    /**
     * Balance converted in currency units
     */
    public BigInteger balanceValue;
    /**
     * Points to be paid
     */
    public BigInteger paidPoint;
    /**
     * Amount of points to be paid converted into currency units
     */
    public BigInteger paidValue;
    /**
     * Points to be paid as fees
     */
    public BigInteger feePoint;
    /**
     * Amount of points to be paid as fees converted into currency units
     */
    public BigInteger feeValue;
    /**
     * the sum of the fees and the points to be paid
     */
    public BigInteger totalPoint;
    /**
     * the sum of fees and points to be paid in currency units
     */
    public BigInteger totalValue;

    /**
     * Constructor
     * @param account Wallet address
     * @param amount Purchase amount
     * @param currency Currency symbol
     * @param balance Balance of point
     * @param balanceValue Balance converted in currency units
     * @param paidPoint Points to be paid
     * @param paidValue Amount of points to be paid converted into currency units
     * @param feePoint Points to be paid as fees
     * @param feeValue Amount of points to be paid as fees converted into currency units
     * @param totalPoint the sum of the fees and the points to be paid
     * @param totalValue the sum of fees and points to be paid in currency units
     */
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
