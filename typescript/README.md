# acc-service-sdk for Typescript

## Installation

Use [yarn](https://yarnpkg.com/) to install acc-service-sdk.

```bash
yarn add acc-service-sdk
```

---

## Testing

To execute library tests just run:

```bash
yarn test
```

---

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

```typescript
// The private key of wallet
const privateKeyOfCollector = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
// The address of wallet, This is the address where token assets are stored
const addressOfAsset = "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714";
// Create a client
const savePurchaseClient = new SavePurchaseClient(NetWorkType.testnet, privateKeyOfCollector, addressOfAsset);
```

### Save New Purchase Data

```typescript
const purchaseId = "P100000000000000";
const timestamp = BigInt(new Date().getTime() / 1000);
await savePurchaseClient.saveNewPurchase(
    purchaseId,
    timestamp,
    0n,
    10_000,
    10_000,
    "php",
    shopId,
    userAccount,
    userPhone,
    [
        {
            productId: "2020051310000000",
            amount: 10_000,
            providePercent: 10,
        },
    ]
);
```

### Save Cancel Purchase Data

```typescript
await savePurchaseClient.saveCancelPurchase(purchaseId, timestamp, 60n);
```

---

## How to use loyalty points

See [API Docs - https://relay.test.acccoin.io/docs/](https://relay.test.acccoin.io/docs/#/Payment)

This is a necessary function to build a point payment system.  
Please create a wallet to be used for payment, and forward the address of the wallet to the operations team of the loyalty system.  
The private key of the wallet, which can be used on testnet, is "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276".  
Please make the wallet of the main net yourself and deliver only its address to the operation team.  

### Create Client for Payment

```typescript
// This is the private key of the wallet to be used for payment.
const privateKeyForPayment = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
const paymentClient = new PaymentClient(NetWorkType.testnet, privateKeyForPayment);
```

### Create Event Collector

### Implement Listener

```typescript
class PaymentEventListener implements ITaskEventListener {
    public onNewPaymentEvent(type: string, code: number, message: string, sequence: bigint, data: IPaymentTaskItem) {
        console.log(`type: ${type.toString()}`);
        console.log(`code: ${code.toString()}`);
        console.log(`message: ${message}`);
        console.log(`sequence: ${sequence.toString()}`);
        console.log(`data: ${JSON.stringify(data)}`);
    }
    public onNewShopEvent(type: string, code: number, message: string, sequence: bigint, data: IShopTaskItem) {
        console.log(`type: ${type.toString()}`);
        console.log(`code: ${code.toString()}`);
        console.log(`message: ${message}`);
        console.log(`sequence: ${sequence.toString()}`);
        console.log(`data: ${JSON.stringify(data)}`);
    }
}
```

### Create Collector

```typescript
const eventCollector = new TaskEventCollector(paymentClient, new PaymentEventListener());
```

### Start Event Collector
```typescript
await eventCollector.start();
```

### Open New Payment
```typescript
const purchaseId = "P1000000000000";
const paymentItem = await paymentClient.openNewPayment(
    purchaseId,
    temporaryAccount,
    BOACoin.make(1000).value,
    "php",
    shopClient.getShopId(),
    terminalID
);
```

### Close New Payment
```typescript
const res = await paymentClient.closeNewPayment(paymentItem.paymentId, true);
```

### Open Cancel Payment
```typescript
const res = await paymentClient.openCancelPayment(paymentItem.paymentId, terminalID);
```

### Close Cancel Payment
```typescript
const res = await paymentClient.closeCancelPayment(paymentItem.paymentId, true);
```

### Stop Event Collector
```typescript
await eventCollector.stop();
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

```typescript
const providerClient = new ProviderClient(
    NetWorkType.testnet,
    "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c" // address: 0x64D111eA9763c93a003cef491941A011B8df5a49
);
```

### Provide to wallet address without agent

```typescript
const receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
const amount = BOACoin.make(100).value;
await providerClient.provideToAddress(providerClient.address, receiver, amount);
```

### Provide to phone number hash without agent

```typescript
const phoneNumber = "+82 10-9000-5000";
const amount = BOACoin.make(100).value;
await providerClient.provideToPhone(providerClient.address, phoneNumber, amount);
```


### Create Client for provide with agent

With agent, you only need to provide the address of the provider to the development team.

```typescript
const providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
const agentClient = new ProviderClient(
    NetWorkType.testnet,
    "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab" // address: 0x3FE8D00143bd0eAd2397D48ba0E31E5E1268dBfb
);
```

### Provide to wallet address with agent

```typescript
const providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49"
const receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
const amount = BOACoin.make(100).value;
await agentClient.provideToAddress(providerAddress, receiver, amount);
```

### Provide to phone number hash with agent

```typescript
const providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49"
const phoneNumber = "+82 10-9000-5000";
const amount = BOACoin.make(100).value;
await agentClient.provideToPhone(prviderAddress, phoneNumber, amount);
```
