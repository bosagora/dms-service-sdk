namespace Acc.Service.Sdk.Test;

using System.Numerics;
using Utils;

public class AmountTest
{
    [SetUp]
    public void Setup()
    {
    }

    [Test]
    public void TestAmount()
    {
        Assert.Multiple(() =>
        {
            Assert.That(Amount.Make("1", 0).ToString(), Is.EqualTo("1"));
            Assert.That(Amount.Make("1", 1).ToString(), Is.EqualTo("10"));
            Assert.That(new Amount(new BigInteger(1), 0).ToString(), Is.EqualTo("1"));
            Assert.That(new Amount(new BigInteger(1), 1).ToString(), Is.EqualTo("1"));

            Assert.That(Amount.Make("1").ToString(), Is.EqualTo("1000000000000000000"));
            Assert.That(Amount.Make("10").ToString(), Is.EqualTo("10000000000000000000"));
            Assert.That(Amount.Make("1.2345678").ToString(), Is.EqualTo("1234567800000000000"));
            Assert.That(Amount.Make("0.0012345").ToString(), Is.EqualTo("1234500000000000"));

            Assert.That(Amount.Make(1).ToString(), Is.EqualTo("1000000000000000000"));
            Assert.That(Amount.Make(10).ToString(), Is.EqualTo("10000000000000000000"));
        });
    }
}
