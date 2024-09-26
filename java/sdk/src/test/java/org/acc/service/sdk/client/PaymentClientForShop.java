package org.acc.service.sdk.client;

import org.acc.service.sdk.data.NetWorkType;
import org.acc.service.sdk.data.payment.PaymentTaskItemShort;
import org.acc.service.sdk.utils.CommonUtils;
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class PaymentClientForShop extends PaymentClient {
    protected String shopId;

    public PaymentClientForShop(NetWorkType network, String privateKey, String shopId) {
        super(network, privateKey);

        this.shopId = shopId;
    }

    public static byte[] getLoyaltyCancelPaymentMessage(
            String paymentId,
            String purchaseId,
            String account,
            long nonce,
            long chainId
    ) {
        String value = TypeEncoder.encode(
                new DynamicStruct(
                        new Bytes32(Numeric.hexStringToByteArray(paymentId)),
                        new Utf8String(purchaseId),
                        new Address(account),
                        new Uint256(chainId),
                        new Uint256(nonce)
                )
        );
        return Hash.sha3(Numeric.hexStringToByteArray(value));
    }

    public String getShopId() {
        return this.shopId;
    }

    public PaymentTaskItemShort approveCancelPayment(
            String paymentId,
            String purchaseId,
            Boolean approval
    ) throws Exception {
        String account = this.credentials.getAddress();
        long nonce = this.getLedgerNonceOf(account);
        byte[] message = getLoyaltyCancelPaymentMessage(
                paymentId,
                purchaseId,
                account,
                nonce,
                this.getChainId()
        );
        String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);
        URI uri = new URI(String.format("%s/v2/payment/cancel/approval", relayEndpoint));
        HttpURLConnection conn = getHttpURLConnection(uri, "POST");

        JSONObject body = new JSONObject();
        body.put("paymentId", paymentId);
        body.put("approval", approval);
        body.put("signature", signature);

        try (OutputStream output = conn.getOutputStream()) {
            output.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        return PaymentTaskItemShort.fromJSONObject(getJSONObjectResponse(conn));
    }
}
