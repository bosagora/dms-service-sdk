package org.dms.service.sdk.data;

import org.json.JSONObject;

public class UserBalance {
    public Balance point;
    public Balance token;

    public UserBalance (JSONObject point, JSONObject token) {
        this.point = new Balance(point.getBigInteger("balance"), point.getBigInteger("value"));
        this.token = new Balance(token.getBigInteger("balance"), token.getBigInteger("value"));
    }
}
