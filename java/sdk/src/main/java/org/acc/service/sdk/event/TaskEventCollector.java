package org.acc.service.sdk.event;

import org.acc.service.sdk.client.PaymentClient;
import org.acc.service.sdk.data.payment.PaymentTaskItem;
import org.acc.service.sdk.data.payment.ShopTaskItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class TaskEventCollector extends Scheduler {

    private PaymentClient client = null;
    private ITaskEventListener listener = null;
    private long sequence = 0;

    public TaskEventCollector(PaymentClient client, ITaskEventListener listener) {
        this.client = client;
        this.listener = listener;
    }

    public void onStart() {
        System.out.println("TaskEventCollector:onStart");
        try {
            this.sequence = this.client.getLatestTaskSequence();
            System.out.printf("Received sequence = %d\n", this.sequence);
        } catch (Exception ignored) {

        }
    }

    public void onWork() {
        try {
            JSONArray tasks = this.client.getTasks(this.sequence);
            for (int idx = 0; idx < tasks.length(); idx++) {
                JSONObject task = tasks.getJSONObject(idx);
                String dataType = task.getString("type");
                int code = task.getInt("code");
                String message = task.getString("message");
                long sequence = task.getLong("sequence");
                if (sequence > this.sequence) this.sequence = sequence;
                System.out.printf("Received sequence = %d\n", this.sequence);

                JSONObject data = task.getJSONObject("data");
                if (Objects.equals(dataType, "pay_new") || Objects.equals(dataType, "pay_cancel")) {
                    PaymentTaskItem payment = PaymentTaskItem.fromJSONObject(data);
                    this.listener.onNewPaymentEvent(
                            dataType,
                            code,
                            message,
                            sequence,
                            payment
                    );
                } else {
                    ShopTaskItem shop = ShopTaskItem.fromJSONObject(data);
                    this.listener.onNewShopEvent(
                            dataType,
                            code,
                            message,
                            sequence,
                            shop
                    );
                }
            }
            Thread.sleep(2000);
        } catch (Exception ignored) {

        }
    }

    public void onStop() {
        System.out.println("TaskEventCollector:onStop");
    }
}
