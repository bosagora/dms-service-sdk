using System.Numerics;

namespace Acc.Service.Sdk.Utils;

/// <summary>
///     The class that defines the Amount
/// </summary>
public class Amount(BigInteger value, uint decimals = 18)
{
    public BigInteger Value { get; } = value;
    public uint Decimals { get; } = decimals;

    public static Amount Make(int pAmount, uint decimals = 18)
    {
        return Make(pAmount.ToString(), decimals);
    }

    public static Amount Make(string value, uint decimals = 18)
    {
        if (value.Equals("")) return new Amount(new BigInteger(0), decimals);
        value = value.Replace(",", "").Replace("_", "");
        var zeroString = "";
        for (var idx = 0; idx < decimals; idx++)
            zeroString += "0";
        var numbers = value.Split('.');
        if (numbers.Length == 1) return new Amount(BigInteger.Parse(numbers[0] + zeroString), decimals);
        var pointString = numbers[1];
        if (pointString.Length > decimals) pointString = pointString[..(int)decimals];
        else if (pointString.Length < decimals) pointString = pointString.PadRight((int)decimals, '0');
        var integral = BigInteger.Parse(numbers[0] + zeroString);
        return new Amount(BigInteger.Add(integral, BigInteger.Parse(pointString)), decimals);
    }

    public string ToAmountString()
    {
        var factor = BigInteger.Pow(new BigInteger(10), (int)Decimals);
        var integral = BigInteger.Divide(Value, factor);
        var decimals = BigInteger.Subtract(Value, BigInteger.Multiply(integral, factor));
        var integralString = integral.ToString();
        var decimalsString = decimals.ToString();
        if (decimalsString.Length < Decimals) decimalsString = decimalsString.PadLeft((int)Decimals, '0');
        return integralString + "." + decimalsString;
    }

    public override string ToString()
    {
        return Value.ToString();
    }
}