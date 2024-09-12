package org.dms.service.sdk.client;

import org.dms.service.sdk.data.NetWorkType;
import org.dms.service.sdk.data.UserBalance;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Client {
    protected final String relayEndpoint;
    protected final String saveEndpoint;
    protected int chainId;

    public Client(NetWorkType network) {
        if (network == NetWorkType.localhost) {
            relayEndpoint = "http://127.0.0.1:7070";
            saveEndpoint = "http://127.0.0.1:3030";
        } else if (network == NetWorkType.mainnet) {
            relayEndpoint = "https://relay.main.acccoin.io";
            saveEndpoint = "https://save.main.acccoin.io";
        } else {
            relayEndpoint = "https://relay.test.acccoin.io";
            saveEndpoint = "https://save.test.acccoin.io";
        }
        chainId = 0;

    }

    protected static JSONObject getResponse(@NotNull HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        br.close();

        JSONObject jObject = new JSONObject(sb.toString());
        int code = jObject.getInt("code");
        if (code != 0) {
            String errorMessage;
            try {
                JSONObject error = jObject.getJSONObject("error");
                errorMessage = String.format("%s (%d)", error.getString("message"), jObject.getInt("code"));
            } catch (Exception e) {
                errorMessage = String.format("%d", jObject.getInt("code"));
            }
            throw new Exception("Internal Error : " + errorMessage);
        }
        return jObject;
    }

    protected static JSONObject getJSONObjectResponse(@NotNull HttpURLConnection conn) throws Exception {
        return getResponse(conn).getJSONObject("data");
    }

    protected static JSONArray getJSONArrayResponse(@NotNull HttpURLConnection conn) throws Exception {
        return getResponse(conn).getJSONArray("data");
    }

    protected HttpURLConnection getHttpURLConnection(@NotNull URI uri, String method) throws Exception  {
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);
        return conn;
    }

    /**
     * Provide the ID of the chain
     */
    public long getChainId() throws Exception {
        if (chainId != 0) {
            return chainId;
        }
        URI uri = new URI(relayEndpoint + "/v1/chain/side/id");
        HttpURLConnection conn = getHttpURLConnection(uri, "GET");
        JSONObject data = getJSONObjectResponse(conn);
        chainId = data.getInt("chainId");
        return chainId;
    }


    /**
     * Provide the user's points and token balance information
     * @param phoneNumber User's phone number
     */
    public UserBalance getBalancePhone(@NotNull String phoneNumber) throws Exception {
        URI uri = new URI(relayEndpoint + "/v1/ledger/balance/phone/" + phoneNumber.trim().replace(" ", "%20"));
        HttpURLConnection conn = getHttpURLConnection(uri, "GET");
        JSONObject data = getJSONObjectResponse(conn);
        return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneHash User's phone number hash
     */
    public UserBalance getBalancePhoneHash(@NotNull String phoneHash) throws Exception {
        URI uri = new URI(relayEndpoint + "/v1/ledger/balance/phoneHash/" + phoneHash.trim());
        HttpURLConnection conn = getHttpURLConnection(uri, "GET");
        JSONObject data = getJSONObjectResponse(conn);
        return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
    }

    /**
     * Provide the user's points and token balance information
     * @param account User's wallet address
     */
    public UserBalance getBalanceAccount(@NotNull String account) throws Exception {
        URI uri = new URI(relayEndpoint + "/v1/ledger/balance/account/" + account.trim());
        HttpURLConnection conn = getHttpURLConnection(uri, "GET");
        JSONObject data = getJSONObjectResponse(conn);
        return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
    }

    /**
     * Provide a nonce corresponding to the user's wallet address. It provides a nonce corresponding to the user's wallet address.
     * This ensures that the same signature is not repeated. And this value is recorded in Contract and automatically increases by 1.
     * @param account User's wallet address
     */
    public long getLedgerNonceOf(@NotNull String account) throws Exception {
        URI uri = new URI(relayEndpoint + "/v1/ledger/nonce/" + account.trim());
        HttpURLConnection conn = getHttpURLConnection(uri, "GET");
        JSONObject data = getJSONObjectResponse(conn);
        return data.getInt("nonce");
    }
}
