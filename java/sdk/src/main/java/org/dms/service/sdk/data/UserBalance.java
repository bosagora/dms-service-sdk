package org.dms.service.sdk.data;

import org.json.JSONObject;

/**
 * Balance data for points and tokens
 */
public class UserBalance {
    /**
     * Balance of Point
     */
    public Balance point;
    /**
     * Balance of Token
     */
    public Balance token;

    /**
     * Constructor
     * @param point Balance of Point
     * @param token Balance of Token
     */
    public UserBalance (JSONObject point, JSONObject token) {
        this.point = new Balance(point.getBigInteger("balance"), point.getBigInteger("value"));
        this.token = new Balance(token.getBigInteger("balance"), token.getBigInteger("value"));
    }
}
