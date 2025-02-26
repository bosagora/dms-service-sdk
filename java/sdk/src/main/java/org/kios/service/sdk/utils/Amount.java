package org.kios.service.sdk.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class Amount {
    private final BigInteger _value;
    private final int _decimals;


    public Amount(BigInteger value) {
        this(value, 18);
    }

    public Amount(BigInteger value, int decimals) {
        this._value = value;
        this._decimals = decimals;
    }

    public BigInteger getValue() {
        return this._value;
    }

    public int getDecimals() {
        return this._decimals;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Amount make(String value, int decimals) {
        if (value.isEmpty()) return new Amount(BigInteger.valueOf(0), decimals);
        value = value.replace(",", "").replace("_", "");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Math.max(0, decimals); i++){
            sb.append("0");
        }
        String Zero = sb.toString();
        String [] numbers = value.split("\\.");
        if (numbers.length == 1) return new Amount(new BigInteger(numbers[0] + Zero, 10));
        String strDecimals = numbers[1];
        if (strDecimals.length() > decimals) strDecimals = strDecimals.substring(0, decimals);
        else if (strDecimals.length() < decimals) {
            int n = decimals - strDecimals.length();

            StringBuilder sb2 = new StringBuilder();
            for(int i = 0; i < Math.max(0, decimals - strDecimals.length()); i++){
                sb2.append("0");
            }
            strDecimals = strDecimals + sb2.toString();
        }
        BigInteger n1 = new BigInteger(numbers[0] + Zero, 10);
        BigInteger n2 = new BigInteger(strDecimals, 10);

        return new Amount(n1.add(n2), decimals);
    }

    @NotNull
    @Contract("_ -> new")
    public static Amount make(String value) {
        return Amount.make(value, 18);
    }

    public String toString() {
        return this._value.toString();
    }

    public String toAmountString() {
        BigInteger factor = BigInteger.valueOf(10).pow(this._decimals);
        BigInteger integral = this._value.divide(factor);
        BigInteger decimal = this._value.subtract(integral.multiply(factor));
        String integral_string = integral.toString();
        String decimals_string = decimal.toString();
        if (decimals_string.length() < this._decimals) {
            StringBuilder sb3 = new StringBuilder();
            for(int i = 0; i < Math.max(0, this._decimals - decimals_string.length()); i++){
                sb3.append("0");
            }
            decimals_string = sb3.toString() + decimals_string;
        }
        return integral_string + "." + decimals_string;
    }
}
