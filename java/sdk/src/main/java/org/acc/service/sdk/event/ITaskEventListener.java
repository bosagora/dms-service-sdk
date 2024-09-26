package org.acc.service.sdk.event;

import org.acc.service.sdk.data.payment.PaymentTaskItem;
import org.acc.service.sdk.data.payment.ShopTaskItem;

public interface ITaskEventListener {
    void onNewPaymentEvent(
            String type,
            int code,
            String message,
            long sequence,
            PaymentTaskItem paymentTaskItem
    );

    void onNewShopEvent(
            String type,
            int code,
            String message,
            long sequence,
            ShopTaskItem shopTaskItem
    );
}

