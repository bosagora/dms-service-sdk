package org.acc.service.sdk.client;

import org.acc.service.sdk.data.NetWorkType;
import org.acc.service.sdk.data.payment.PaymentInfo;
import org.acc.service.sdk.data.payment.PaymentTaskItem;
import org.acc.service.sdk.data.payment.ShopTaskItem;
import org.acc.service.sdk.event.ITaskEventListener;
import org.acc.service.sdk.event.TaskEventCollector;
import org.acc.service.sdk.utils.Amount;
import org.acc.service.sdk.utils.CommonUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentClientTest {
    @Test
    void PaymentClient() {
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
            assertEquals(value.account, "0x64D111eA9763c93a003cef491941A011B8df5a49");

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
            System.out.println("[ Waiting... ]t");
            Thread.sleep(3000);

            // Close Cancel Payment
            System.out.println("[ Close Cancel Payment ]");
            client.closeCancelPayment(paymentItem.paymentId, true);

            PaymentTaskItem paymentTaskItem = client.getPaymentItem(paymentItem.paymentId);
            assertEquals(paymentTaskItem.paymentId, paymentItem.paymentId);

            // Waiting...
            System.out.println("[ Waiting... ]");
            Thread.sleep(3000);

            // Stop Event Collector
            Thread.sleep(3000);
            collector.stop();

        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
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
