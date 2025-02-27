package org.kios.service.sdk.data.purchase;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ResponseSavePurchase {
    public int type;
    public String sequence;
    public String purchaseId;

    public ResponseSavePurchase(int type, String sequence, String purchaseId) {
        this.type = type;
        this.sequence = sequence;
        this.purchaseId = purchaseId;
    }

    @NotNull
    @Contract("_ -> new")
    public static ResponseSavePurchase fromJSONObject(JSONObject data) {
        if (data.has("tx")) {
            JSONObject tx = data.getJSONObject("tx");
            return new ResponseSavePurchase(
                    tx.getInt("type"),
                    tx.getString("sequence"),
                    tx.getString("purchaseId"));
        } else {
            return new ResponseSavePurchase(0, "0", "");
        }
    }
}
