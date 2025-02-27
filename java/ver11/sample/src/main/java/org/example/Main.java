package org.example;

import org.kios.service.sdk.client.Client;
import org.kios.service.sdk.client.PaymentClient;
import org.kios.service.sdk.client.SavePurchaseClient;
import org.kios.service.sdk.data.NetWorkType;
import org.kios.service.sdk.data.UserBalance;
import org.kios.service.sdk.data.payment.PaymentInfo;
import org.kios.service.sdk.data.payment.PaymentTaskItem;
import org.kios.service.sdk.data.payment.ShopTaskItem;
import org.kios.service.sdk.data.purchase.PurchaseDetail;
import org.kios.service.sdk.data.purchase.ResponseSavePurchase;
import org.kios.service.sdk.event.ITaskEventListener;
import org.kios.service.sdk.event.TaskEventCollector;
import org.kios.service.sdk.utils.Amount;
import org.kios.service.sdk.utils.CommonUtils;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        testClient();
        testPayment();
        testSavePurchase();
    }

    public static void testClient() {
        Client client = new Client(NetWorkType.testnet);

        System.out.println("getChainId");
        try {
            long value = client.getChainId();
            if (value != 215115) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }

        System.out.println("getBalancePhone");
        try {
            UserBalance balance = client.getBalancePhone("+82 10-1000-2099");
            if (!balance.point.balance.toString().equals("5000000000000000000000000")) {
                System.out.println("Error");
            }
            if (!balance.token.balance.toString().equals("0")) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }

        System.out.println("getBalancePhoneHash");
        try {
            UserBalance balance = client.getBalancePhoneHash("0x6e2f492102956a83a350152070be450b44fa19c08455c74b3aa79cc74195d3ba");
            if (!balance.point.balance.toString().equals("5000000000000000000000000")) {
                System.out.println("Error");
            }
            if (!balance.token.balance.toString().equals("0")) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }

        System.out.println("getBalanceAccount");
        try {
            UserBalance balance = client.getBalanceAccount("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563");
            if (!balance.point.balance.toString().equals("5000000000000000000000000")) {
                System.out.println("Error");
            }
            if (!balance.token.balance.toString().equals("100000000000000000000000")) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }

        System.out.println("getLedgerNonceOf");
        try {
            long nonce = client.getLedgerNonceOf("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563");
            if (nonce < 0) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }

        System.out.println("getInternationalPhoneNumber");
        try {
            String res = CommonUtils.getInternationalPhoneNumber("+82 010 1000 2099");
            if (!res.equals("+82 10-1000-2099")) {
                System.out.println("Error");
            }
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }
    }

    public static void testPayment() {
        try {
            // Create Client
            System.out.println("[ Create Client ]");
            String privateKeyForPayment = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
            PaymentClient client = new PaymentClient(NetWorkType.testnet, privateKeyForPayment);

            // Create Event Collector
            System.out.println("[ Create Event Collector ]");
            TaskEventListener listener = new TaskEventListener();
            TaskEventCollector collector = new TaskEventCollector(client, listener);

            // Create User Client
            System.out.println("[ Create User Client ]");
            PaymentClientForUser userClient = new PaymentClientForUser(NetWorkType.testnet, "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");

            // Create Shop Client
            System.out.println("[ Create Shop Client ]");
            PaymentClientForShop shopClient = new PaymentClientForShop(NetWorkType.testnet, "0xa237d68cbb66fd5f76e7b321156c46882546ad87d662dec8b82703ac31efbf0a", "0x0001be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874");

            // Start Event Collector
            collector.start();
            Thread.sleep(3000);

            // Create Temporary Account
            System.out.println("[ Create Temporary Account ]");
            String temporaryAccount = userClient.getTemporaryAccount();
            System.out.printf("  - Temporary Account: %s\n", temporaryAccount);

            String terminalID = "POS001";

            //  Get Payment Info
            PaymentInfo value = client.getPaymentInfo("0x64D111eA9763c93a003cef491941A011B8df5a49", BigInteger.valueOf(100000000), "php");
            if (!value.account.equals("0x64D111eA9763c93a003cef491941A011B8df5a49")) {
                System.out.println("Error");
            }

            // Open New Payment
            System.out.println("[ Open New Payment ]");
            PaymentTaskItem paymentItem = client.openNewPayment(
                    CommonUtils.getSamplePurchaseId(),
                    temporaryAccount,
                    Amount.make("1_000").getValue(),
                    "php",
                    shopClient.getShopId(),
                    terminalID
            );

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Approval New Payment
            System.out.println("[ Approval New Payment ]");
            userClient.approveNewPayment(
                    paymentItem.paymentId,
                    paymentItem.purchaseId,
                    paymentItem.amount,
                    paymentItem.currency,
                    paymentItem.shopId,
                    true
            );

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Close New Payment
            System.out.println("[ Close New Payment ]");
            client.closeNewPayment(paymentItem.paymentId, true);

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Open Cancel Payment
            System.out.println("[ Open Cancel Payment ]");
            client.openCancelPayment(paymentItem.paymentId, terminalID);

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Approval Cancel Payment
            System.out.println("[ Approval Cancel Payment ]");
            shopClient.approveCancelPayment(
                    paymentItem.paymentId,
                    paymentItem.purchaseId,
                    true
            );

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Close Cancel Payment
            System.out.println("[ Close Cancel Payment ]");
            client.closeCancelPayment(paymentItem.paymentId, true);

            PaymentTaskItem paymentTaskItem = client.getPaymentItem(paymentItem.paymentId);
            if (!paymentTaskItem.paymentId.equals(paymentItem.paymentId)) {
                System.out.println("Error");
            }

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Stop Event Collector
            Thread.sleep(3000);
            collector.stop();

        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }
    }

    public static void testSavePurchase() {
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

        try {
            // Check Balance
            System.out.println("[ Check Balance ]");
            UserBalance balance1 = savePurchaseClient.getBalanceAccount(userAccount);
            System.out.printf("  - Balance: %s\n", new Amount(balance1.point.balance).toAmountString());

            // Save New Purchase
            System.out.println("[ Save New Purchase ]");
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
            System.out.printf("  - type: %d, sequence: %s, purchaseId: %s\n", res1.type, res1.sequence, res1.purchaseId);

            // Waiting...
            System.out.println("[ Waiting for providing... ]");
            long t1 = CommonUtils.getTimeStamp();
            while (true) {
                UserBalance balance2 = savePurchaseClient.getBalanceAccount(userAccount);
                if (balance2.point.balance.equals(balance1.point.balance.add(Amount.make("1000").getValue()))) {
                    break;
                } else if (CommonUtils.getTimeStamp() - t1 > 120) {
                    System.out.println("Time out for providing... ");
                    break;
                }
                Thread.sleep(1000);
            }

            // Check Balance
            System.out.println("[ Check Balance ]");
            UserBalance balance3 = savePurchaseClient.getBalanceAccount(userAccount);
            if (!balance3.point.balance.equals(balance1.point.balance.add(Amount.make("1000").getValue()))) {
                System.out.println("Error");
            }
            System.out.printf("  - Balance: %s\n", new Amount(balance3.point.balance).toAmountString());

            // Save New Purchase
            System.out.println("[ Save New Purchase and Cancel ]");
            String purchaseId = CommonUtils.getSamplePurchaseId();
            long timestamp = CommonUtils.getTimeStamp();
            ResponseSavePurchase res2 = savePurchaseClient.saveNewPurchase(
                    purchaseId,
                    timestamp,
                    60,
                    "10000",
                    "10000",
                    "php",
                    shopId,
                    userAccount,
                    "",
                    new PurchaseDetail[]{new PurchaseDetail("2020051310000000", "10000", 10)}
            );
            System.out.printf("  - type: %d, sequence: %s, purchaseId: %s\n", res2.type, res2.sequence, res2.purchaseId);

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            System.out.println("[ Save Cancel ]");
            ResponseSavePurchase res3 = savePurchaseClient.saveCancelPurchase(purchaseId, timestamp, 0);
            System.out.printf("  - type: %d, sequence: %s, purchaseId: %s\n", res3.type, res3.sequence, res3.purchaseId);

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(50000);

            // Check Balance
            System.out.println("[ Check Balance ]");
            UserBalance balance4 = savePurchaseClient.getBalanceAccount(userAccount);
            if (!balance4.point.balance.equals(balance3.point.balance)) {
                System.out.println("Error");
            }
            System.out.printf("  - Balance: %s\n", new Amount(balance4.point.balance).toAmountString());
        } catch (Exception e) {
            System.out.printf("some exception message... %s", e.getMessage());
        }
    }
}

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
