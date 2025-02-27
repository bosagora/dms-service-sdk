package org.kios.service.sdk.data.payment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Data generated during store information modification
 */
public class ShopTaskItem {
    public String taskId;
    public String shopId;
    public String name;
    public String currency;
    public int status;
    public String account;
    public String terminalId;
    public int taskStatus;

    public ShopTaskItem(
            String taskId,
            String shopId,
            String name,
            String currency,
            int status,
            String account,
            String terminalId,
            int taskStatus
    ) {
        this.taskId = taskId;
        this.shopId = shopId;
        this.name = name;
        this.currency = currency;
        this.status = status;
        this.account = account;
        this.terminalId = terminalId;
        this.taskStatus = taskStatus;
    }

    @NotNull
    @Contract("_ -> new")
    public static ShopTaskItem fromJSONObject(JSONObject data) {
        return new ShopTaskItem(
                data.getString("taskId"),
                data.getString("shopId"),
                data.getString("name"),
                data.getString("currency"),
                data.getInt("status"),
                data.getString("account"),
                data.getString("terminalId"),
                data.getInt("taskStatus")
        );
    }

    public ShopTaskItem cloneTaskItem() {
        return new ShopTaskItem(
            this.taskId,
            this.shopId,
            this.name,
            this.currency,
            this.status,
            this.account,
            this.terminalId,
            this.taskStatus
        );
    }
}
