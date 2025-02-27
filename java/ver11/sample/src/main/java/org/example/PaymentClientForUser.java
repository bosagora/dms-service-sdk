package org.example;

import org.kios.service.sdk.client.PaymentClient;
import org.kios.service.sdk.data.NetWorkType;
import org.kios.service.sdk.data.payment.PaymentTaskItemShort;
import org.kios.service.sdk.utils.CommonUtils;
import org.json.JSONObject;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class PaymentClientForUser extends PaymentClient {

    public PaymentClientForUser(NetWorkType network, String privateKey) {
        super(network, privateKey);
    }

    public static byte[] getLoyaltyNewPaymentMessage(
            String paymentId,
            String purchaseId,
            BigInteger amount,
            String currency,
            String shopId,
            String account,
            long nonce,
            long chainId
    ) {
        String value = TypeEncoder.encode(
                new DynamicStruct(
                        new Bytes32(Numeric.hexStringToByteArray(paymentId)),
                        new Utf8String(purchaseId),
                        new Uint256(amount),
                        new Utf8String(currency),
                        new Bytes32(Numeric.hexStringToByteArray(shopId)),
                        new Address(account),
                        new Uint256(chainId),
                        new Uint256(nonce)
                )
        );
        return Hash.sha3(Numeric.hexStringToByteArray(value));
    }

    public String getTemporaryAccount() throws Exception {
        String account = this.credentials.getAddress();
        long nonce = this.getLedgerNonceOf(account);
        byte[] message = CommonUtils.getAccountMessage(
                account,
                nonce,
                this.getChainId()
        );
        String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);
        URI uri = new URI(String.format("%s/v2/payment/account/temporary", relayEndpoint));
        HttpURLConnection conn = getHttpURLConnection(uri, "POST");

        JSONObject body = new JSONObject();
        body.put("account", account);
        body.put("signature", signature);

        try (OutputStream output = conn.getOutputStream()) {
            output.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        JSONObject data = getJSONObjectResponse(conn);
        return data.getString("temporaryAccount");
    }

    public PaymentTaskItemShort approveNewPayment(
            String paymentId,
            String purchaseId,
            BigInteger amount,
            String currency,
            String shopId,
            Boolean approval
    ) throws Exception {
        String account = this.credentials.getAddress();
        long nonce = this.getLedgerNonceOf(account);
        byte[] message = getLoyaltyNewPaymentMessage(
                paymentId,
                purchaseId,
                amount,
                currency,
                shopId,
                account,
                nonce,
                this.getChainId()
        );
        String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);
        try {
            URI uri = new URI(String.format("%s/v2/payment/new/approval", relayEndpoint));
            HttpURLConnection conn = getHttpURLConnection(uri, "POST");

            JSONObject body = new JSONObject();
            body.put("paymentId", paymentId);
            body.put("approval", approval);
            body.put("signature", signature);

            try (OutputStream output = conn.getOutputStream()) {
                output.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            return PaymentTaskItemShort.fromJSONObject(getJSONObjectResponse(conn));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
    }
}
