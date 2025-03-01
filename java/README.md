# dms-service-sdk for JAVA

## Installation

### Install gradle

See) https://gradle.org/install

### Publish to MavenLocal

```shell
./gradlew clean publishToMavenLocal
```
You can verify that the library is distributed in the storage `~/.m2/repository`.

### How to use SDK in your project

Please set the contents of the file `build.gradle` as below

``` 
repositories {
    mavenLocal()
}

dependencies {
    implementation "org.json:json:20231013"
    implementation "com.googlecode.libphonenumber:libphonenumber:8.13.44"
    implementation "org.web3j:core:4.12.1"
    implementation 'org.dms.service.sdk:core:1.0.0-SNAPSHOT'
}
```

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

```java
String privateKeyOfCollector = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
String addressOfAsset = "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714";
SavePurchaseClient savePurchaseClient = new SavePurchaseClient(
    NetWorkType.testnet,
    privateKeyOfCollector,
    addressOfAsset
);
String shopId = "0x0001be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";
String userAccount = "0x64D111eA9763c93a003cef491941A011B8df5a49";
String userPhone = "";
```

### Save New Purchase Data

```java
ResponseSavePurchase res1 = savePurchaseClient.saveNewPurchase(
    CommonUtils.getSamplePurchaseId(),
    CommonUtils.getTimeStamp(),
    0,
    "10000",
    "10000",
    "php",
    shopId,
    userAccount,
    "",
    new PurchaseDetail[]{new PurchaseDetail("2020051310000000", "10000", 10)}
);
```

### Save Cancel Purchase Data
```java
ResponseSavePurchase res3 = savePurchaseClient.saveCancelPurchase(purchaseId, timestamp, 0);
```

---

## How to use loyalty points

See [API Docs - https://relay.test.acccoin.io/docs/](https://relay.test.acccoin.io/docs/#/Payment)

This is a necessary function to build a point payment system.  
Please create a wallet to be used for payment, and forward the address of the wallet to the operations team of the loyalty system.  
The private key of the wallet, which can be used on testnet, is "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276".  
Please make the wallet of the main net yourself and deliver only its address to the operation team.  

### Create Client for Payment

```java
// This is the private key of the wallet to be used for payment.
String privateKeyForPayment = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
PaymentClient client = new PaymentClient(NetWorkType.testnet, privateKeyForPayment);
```

### Create Event Collector

### Implement Listener

```java
class TaskEventListener implements ITaskEventListener {
    public void onNewPaymentEvent(
            String type,
            int code,
            String message,
            long sequence,
            PaymentTaskItem paymentTaskItem
    ) {
        System.out.printf("  -> onNewPaymentEvent %s - %d - %s - %d\n", type, code, message, sequence);
    }

    public void onNewShopEvent(
            String type,
            int code,
            String message,
            long sequence,
            ShopTaskItem shopTaskItem
    ) {
        System.out.printf("  -> onNewShopEvent %s - %d - %s - %d\n", type, code, message, sequence);
    }
}
```

### Create Collector

```java
TaskEventListener listener = new TaskEventListener();
TaskEventCollector collector = new TaskEventCollector(client, listener);
```

### Start Event Collector
```java
collector.start();
```

### Open New Payment
```java
PaymentTaskItem paymentItem = client.openNewPayment(
    CommonUtils.getSamplePurchaseId(),
    temporaryAccount,
    Amount.make("1_000").getValue(),
    "php",
    shopClient.getShopId(),
    terminalID
);

```

### Close New Payment
```java
PaymentTaskItem paymentItem = client.closeNewPayment(paymentItem.paymentId, true);
```

### Open Cancel Payment
```java
PaymentTaskItem paymentItem = client.openCancelPayment(paymentItem.paymentId, terminalID);
```

### Close Cancel Payment
```java
PaymentTaskItem paymentItem = client.closeCancelPayment(paymentItem.paymentId, true);
```

### Stop Event Collector
```java
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

```java
ProviderClient providerClient = new ProviderClient(NetWorkType.testnet,
    "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
```

### Provide to wallet address without agent

```java
String receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
BigInteger amount = Amount.make("100").getValue();
String txHash = providerClient.provideToAddress(providerClient.getAddress(), receiver, amount);
```

### Provide to phone number hash without agent

```java
String phoneNumber = "+82 10-9000-5000";
BigInteger amount = Amount.make("100").getValue();
String txHash = providerClient.ProvideToPhone(providerClient.getAddress(), phoneNumber, amount);
```


### Create Client for provide with agent

With agent, you only need to provide the address of the provider to the development team.

```java
String providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
ProviderClient agentClient = new ProviderClient(NetWorkType.testnet, "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab");
```

### Provide to wallet address with agent

```java
String providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
String receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
BigInteger amount = Amount.make("100").getValue();
String txHash = agentClient.provideToAddress(providerAddress, receiver, amount);
```

### Provide to phone number hash with agent

```java
String providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
String phoneNumber = "+82 10-9000-5000";
BigInteger amount = Amount.make("100").getValue();
String txHash = agentClient.ProvideToPhone(providerAddress, phoneNumber, amount);
```
