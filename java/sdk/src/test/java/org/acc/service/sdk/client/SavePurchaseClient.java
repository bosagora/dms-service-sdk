package org.acc.service.sdk.client;

import org.acc.service.sdk.data.NetWorkType;
import org.acc.service.sdk.data.UserBalance;
import org.acc.service.sdk.data.purchase.PurchaseDetail;
import org.acc.service.sdk.data.purchase.ResponseSavePurchase;
import org.acc.service.sdk.utils.Amount;
import org.acc.service.sdk.utils.CommonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SavePurchaseClientTest {
    @Test void SavePurchase() {
        String privateKeyOfCollector = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
        String addressOfAsset = "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714";
        SavePurchaseClient savePurchaseClient = new SavePurchaseClient(NetWorkType.testnet, privateKeyOfCollector, addressOfAsset);
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
                new PurchaseDetail[]{ new PurchaseDetail("2020051310000000", "10000", 10) }
            );
            System.out.printf("  - type: %d, sequence: %s, purchaseId: %s\n", res1.type, res1.sequence, res1.purchaseId);

            // Waiting...
            System.out.println("[ Waiting for providing... ]");
            long t1 = CommonUtils.getTimeStamp();
            while(true) {
                UserBalance balance2 = savePurchaseClient.getBalanceAccount(userAccount);
                if (balance2.point.balance.equals(balance1.point.balance.add(Amount.make("1000").getValue()))) {
                    break;
                }
                else if (CommonUtils.getTimeStamp() - t1 > 120) {
                    System.out.println("Time out for providing... ");
                    break;
                }
                Thread.sleep(1000);
            }

            // Check Balance
            System.out.println("[ Check Balance ]");
            UserBalance balance3 = savePurchaseClient.getBalanceAccount(userAccount);
            assertEquals(balance3.point.balance, balance1.point.balance.add(Amount.make("1000").getValue()));
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
                    new PurchaseDetail[]{ new PurchaseDetail("2020051310000000", "10000", 10) }
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
            assertEquals(balance4.point.balance, balance3.point.balance);
            System.out.printf("  - Balance: %s\n", new Amount(balance4.point.balance).toAmountString());

        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }
    }
}
