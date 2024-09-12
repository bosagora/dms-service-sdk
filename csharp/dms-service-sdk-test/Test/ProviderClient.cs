using Nethereum.Util;

namespace Dms.Service.Sdk.Test;

using Client;
using Types;
using Utils;
using System.Numerics;

public class ProviderClientTests
{
    private ProviderClient providerClient;
    private ProviderClient agentClient;

    public ProviderClientTests()
    {
        providerClient = new ProviderClient(NetWorkType.TestNet, "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
        agentClient = new ProviderClient(NetWorkType.TestNet, "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab");
    }

    [SetUp]
    public void Setup()
    {
    }

    [Test]
    public void Test01_IsProvider()
    {
        var value = providerClient.IsProvider(providerClient.Address);
        Assert.That(value, Is.EqualTo(true));
    }

    [Test]
    public void Test02_ClearAgent()
    {
        providerClient.SetAgent(AddressUtil.ZERO_ADDRESS);
        Assert.That(providerClient.GetAgent(), Is.EqualTo(AddressUtil.ZERO_ADDRESS));
    }

    [Test]
    public void Test03_ProvideToAddress()
    {
        var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
        var res1 = providerClient.GetBalanceAccount(receiver);
        var oldBalance = res1.Point.Balance;

        var amount = Amount.Make("100").Value;
        providerClient.ProvideToAddress(providerClient.Address, receiver, amount);
        var res2 = providerClient.GetBalanceAccount(receiver);

        Assert.That(res2.Point.Balance, Is.EqualTo(BigInteger.Add(oldBalance, amount)));
    }

    [Test]
    public void Test04_ProvideToPhone()
    {
        var phoneNumber = "+82 10-9000-5000";
        var res1 = providerClient.GetBalancePhone(phoneNumber);
        var oldBalance = res1.Point.Balance;
        var amount = Amount.Make("100").Value;
        
        providerClient.ProvideToPhone(providerClient.Address, phoneNumber, amount);
        var res2 = providerClient.GetBalancePhone(phoneNumber);

        Assert.That(res2.Point.Balance, Is.EqualTo(BigInteger.Add(oldBalance, amount)));
    }

    [Test]
    public void Test05_SetNewAgent()
    {
        providerClient.SetAgent(agentClient.Address);
        Assert.That(providerClient.GetAgent(), Is.EqualTo(agentClient.Address));
    }
        
    [Test]
    public void Test06_ProvideToAddress()
    {
        var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
        var res1 = providerClient.GetBalanceAccount(receiver);
        var oldBalance = res1.Point.Balance;

        var amount = Amount.Make("100").Value;
        agentClient.ProvideToAddress(providerClient.Address, receiver, amount);
        var res2 = providerClient.GetBalanceAccount(receiver);

        Assert.That(res2.Point.Balance, Is.EqualTo(BigInteger.Add(oldBalance, amount)));
    }
        
    [Test]
    public void Test07_ProvideToPhone()
    {
        var phoneNumber = "+82 10-9000-5000";
        var res1 = providerClient.GetBalancePhone(phoneNumber);
        var oldBalance = res1.Point.Balance;
        var amount = Amount.Make("100").Value;
        
        agentClient.ProvideToPhone(providerClient.Address, phoneNumber, amount);
        var res2 = providerClient.GetBalancePhone(phoneNumber);

        Assert.That(res2.Point.Balance, Is.EqualTo(BigInteger.Add(oldBalance, amount)));
    }
}
