# dms-service-sdk for C#

## Installation

See nuget package) https://www.nuget.org/packages/dms-service-sdk

## Features
A standard development kit provided for interworking with a decentralized loyalty point system.
This SDK can be used in the following places.
1. It can be used when implementing the function of delivering purchase information paid by a KIOSK or POS.
2. It can be used when implementing the ability to purchase products using loyalty points.
3. It can be used when implementing a method in which partners deposit tokens and then provide points to users.

---

## How to save purchase data

See [API Docs - https://save.test.acccoin.io/docs/](https://save.test.acccoin.io/docs/)

This is a function used by partners that support the payment system.  
The test net of the loyalty system is ready.  
You can proceed with the development using the test net and switch to the main net at the time the development is completed.  
Please create a wallet to be used for this feature, and forward the address of the wallet to the operations team of the loyalty system.  
The private key of the wallet, which can be used on testnet, is "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276".  
Please make the wallet of the main net yourself and deliver only its address to the operation team.  

### Create Client Module

```cs
var SavePurchaseClient client = new SavePurchaseClient(
    NetWorkType.TestNet, 
    "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276", // The private key of wallet
    "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714" //  The address of wallet, This is the address where token assets are stored
    );
```

### Save New Purchase Data

```cs
var purchaseId = CommonUtils.GetSamplePurchaseId();
var timestamp = CommonUtils.GetTimeStamp();
await client.SaveNewPurchase(
        purchaseId, 
        timestamp,
        0,
        "10000",
        "10000",
        "php",
        shopId,
        userAccount,
        "",
        new PurchaseDetail[]{ new PurchaseDetail("2020051310000000", "10000", 10) }
    );
```

### Save Cancel Purchase Data
```cs
await client.SaveCancelPurchase(purchaseId, timestamp, 3600);
```

---

## How to use loyalty points

See [API Docs - https://relay.test.acccoin.io/docs/](https://relay.test.acccoin.io/docs/#/Payment)

This is a necessary function to build a point payment system.  
Please create a wallet to be used for payment, and forward the address of the wallet to the operations team of the loyalty system.  
The private key of the wallet, which can be used on testnet, is "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276".  
Please make the wallet of the main net yourself and deliver only its address to the operation team.  
### Create Client for Payment

```cs
// This is the private key of the wallet to be used for payment.
var privateKeyForPayment = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
var paymentClient = new PaymentClient(NetWorkType.TestNet, privateKeyForPayment);
```

### Create Event Collector

### Implement Listener

```cs
public class TestEventListener : ITaskEventListener
{
    public void OnNewPaymentEvent(
        string type,
        int code,
        string message,
        long sequence,
        PaymentTaskItem paymentTaskItem
    )
    {
        Console.WriteLine($"  -> OnNewPaymentEvent {type} - {code} - {message} - {sequence}");
    }

    public void OnNewShopEvent(
        string type,
        int code,
        string message,
        long sequence,
        ShopTaskItem shopTaskItem
    )
    {
        Console.WriteLine($"  -> OnNewShopEvent {type} - {code} - {message} - {sequence}");
    }
}
```

### Create Collector

```cs
var listener = new TestEventListener();
collector = new TaskEventCollector(paymentClient, listener);
```

### Start Event Collector
```cs
collector.Start();
```

### Open New Payment
```cs
var purchaseId = CommonUtils.GetSamplePurchaseId();
var paymentItem = await paymentClient.OpenNewPayment(
    purchaseId,
    temporaryAccount,
    Amount.Make("1_000").Value,
    "php",
    shopId,
    terminalId
);
```

### Close New Payment
```cs
await paymentClient.CloseNewPayment(paymentItem.PaymentId, true);
```

### Open Cancel Payment
```cs
await paymentClient.OpenCancelPayment(paymentItem.PaymentId, terminalId);
```

### Close Cancel Payment
```cs
await paymentClient.CloseCancelPayment(paymentItem.PaymentId, true);
```

### Stop Event Collector
```cs
collector.Stop();
```

---

## How to provide loyalty points

See [API Docs - https://relay.test.acccoin.io/docs/](https://relay.test.acccoin.io/docs/#/Loyalty%20Point%20Provider)

This is the functionality you need to provide points.  
You first need to deposit more than 100,000 tokens through the app.  
And you have to ask the system operation team for a partner.  
You must register first in your app.  
And if you register the agent's address, you don't have to provide the private key of the wallet with the assets.~~

### Create Client for provide without agent

If the agent is not used, the private key of the provider in which the asset is stored should be provided to the development team

```cs
var providerClient = new ProviderClient(NetWorkType.TestNet,
    "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
```

### Provide to wallet address without agent

```cs
var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
var amount = Amount.Make("100").Value;
await providerClient.ProvideToAddress(providerClient.Address, receiver, amount);
```

### Provide to phone number hash without agent

```cs
var phoneNumber = "+82 10-9000-5000";
var amount = Amount.Make("100").Value;
await providerClient.ProvideToPhone(providerClient.Address, phoneNumber, amount);
```


### Create Client for provide with agent

With agent, you only need to provide the address of the provider to the development team.

```cs
var providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
var agentClient = new ProviderClient(NetWorkType.TestNet,
    "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab"); // address: 0x3FE8D00143bd0eAd2397D48ba0E31E5E1268dBfb
```

### Provide to wallet address with agent

```cs
var providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49"
var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
var amount = Amount.Make("100").Value;
await agentClient.ProvideToAddress(prviderAddress, receiver, amount);
```

### Provide to phone number hash with agent

```cs
var providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49"
var phoneNumber = "+82 10-9000-5000";
var amount = Amount.Make("100").Value;
await agentClient.ProvideToAddress(prviderAddress, phoneNumber, amount);
```
