namespace Dms.Service.Sdk.Test;

using Client;
using Types;
using Utils;
using System.Numerics;

public class SavePurchaseClientTests
{
    private SavePurchaseClient client = new SavePurchaseClient(
        NetWorkType.TestNet,
        "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276",
        "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714"
        );
    string shopId = "0x0001be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";
    string userAccount = "0x64D111eA9763c93a003cef491941A011B8df5a49";
    string userPhone = "";

    private UserBalanceData balance1;
    private UserBalanceData balance2;

    private string purchaseId;
    private long timestamp;

    [SetUp]
    public void Setup()
    {
    }

    [Test]
    public async Task Test01_CheckBalance()
    {
        balance1 = await client.GetBalanceAccount(userAccount);
        System.Console.WriteLine($"Balance: {new Amount(balance1.Point.Balance).ToAmountString()}");
    }
    
    [Test]
    public async Task Test02_SaveNewPurchase()
    {
        var res1 = await client.SaveNewPurchase(
            CommonUtils.GetSamplePurchaseId(),
            CommonUtils.GetTimeStamp(),
            0,
            "10000",
            "10000",
            "php",
            shopId,
            userAccount,
            "",
            new PurchaseDetail[]{ new PurchaseDetail("2020051310000000", "10000", 10) }
        );
        System.Console.WriteLine($"type: {res1.Type}, sequence: {res1.Sequence}, purchaseId: {res1.PurchaseId}");
    }

    [Test]
    public async Task Test03_Waiting()
    {
        var t1 = CommonUtils.GetTimeStamp();
        while(true) {
            var balance = await client.GetBalanceAccount(userAccount);
            if (balance.Point.Balance.Equals(BigInteger.Add(balance1.Point.Balance, Amount.Make("1000").Value)))
            {
                break;
            }
            else if (CommonUtils.GetTimeStamp() - t1 > 120)
            {
                System.Console.WriteLine("Time out for providing... ");
                break;
            }
            await Task.Delay(1000);
        }
    }
    
    [Test]
    public async Task Test04_CheckBalance()
    {
        balance2 = await client.GetBalanceAccount(userAccount);
        Assert.That(balance2.Point.Balance,
            Is.EqualTo(BigInteger.Add(balance1.Point.Balance, Amount.Make("1000").Value)));
        System.Console.WriteLine($"Balance: {new Amount(balance2.Point.Balance).ToAmountString()}");
    }

    [Test]
    public async Task Test05_SaveNewPurchase()
    {
        purchaseId = CommonUtils.GetSamplePurchaseId();
        timestamp = CommonUtils.GetTimeStamp();
        var res2 = await client.SaveNewPurchase(
            purchaseId,
            timestamp,
            60,
            "10000",
            "10000",
            "php",
            shopId,
            userAccount,
            "",
            new PurchaseDetail[] { new PurchaseDetail("2020051310000000", "10000", 10) }
        );
        System.Console.WriteLine($"type: {res2.Type}, sequence: {res2.Sequence}, purchaseId: {res2.PurchaseId}");
    }
    
    [Test]
    public async Task Test06_Waiting()
    {
        await Task.Delay(100);
    }
    
    [Test]
    public async Task Test07_SaveCancelPurchase()
    {
        var res3 = await client.SaveCancelPurchase(purchaseId, timestamp, 3600);
        System.Console.WriteLine($"type: {res3.Type}, sequence: {res3.Sequence}, purchaseId: {res3.PurchaseId}");
    }
    
    [Test]
    public async Task Test08_Waiting()
    {
        await Task.Delay(50000);
    }
    
    [Test]
    public async Task Test09_CheckBalance()
    {
        var balance4 = await client.GetBalanceAccount(userAccount);
        System.Console.WriteLine($"Balance: {balance4.Point.Balance.ToString()}");
        
        Assert.That(balance4.Point.Balance,
            Is.EqualTo(balance2.Point.Balance));
    }
}
