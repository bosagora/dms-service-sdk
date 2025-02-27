package org.kios.service.sdk.client;

import org.kios.service.sdk.data.NetWorkType;
import org.kios.service.sdk.data.purchase.*;
import org.kios.service.sdk.utils.Amount;
import org.kios.service.sdk.utils.CommonUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * The client that delivers purchase data to the loyalty system to store purchase data in IPFS.
 * First, you must obtain permission from the loyalty system and register the address of the wallet to be used in the loyalty system.
 */
public class SavePurchaseClient extends Client {
    /**
     * Message repeater's wallet for saving purchases
     */
    protected final Credentials credentials;

    /**
     * wallet address of asset owner
     */
    protected final String assetAddress;

    /**
     * Constructor
     * @param network Type of network (mainnet, testnet, localhost)
     * @param privateKey The private key used in the saving purchases
     * @param assetAddress The wallet address of asset owner
     */
    public SavePurchaseClient(NetWorkType network, String privateKey, String assetAddress) {
        super(network);
        this.credentials = Credentials.create(ECKeyPair.create(new BigInteger(Numeric.cleanHexPrefix(privateKey), 16)));
        this.assetAddress = assetAddress;
    }

    public String getAddress() {
        return this.credentials.getAddress();
    }

    private BigInteger getLoyaltyInTransaction(
        BigInteger cashAmount,
        @NotNull BigInteger totalAmount,
        ArrayList<SaveNewDetail> details
    ) {
        if (totalAmount.equals(BigInteger.ZERO)) return BigInteger.ZERO;
        if (cashAmount.equals(BigInteger.ZERO)) return BigInteger.ZERO;
        BigInteger sum = BigInteger.valueOf(0);
        for (SaveNewDetail elem : details) {
            sum = sum.add(elem.amount.multiply(elem.providePercent));
        }
        return CommonUtils.zeroGWEI(sum.multiply(cashAmount).divide(totalAmount).divide(BigInteger.valueOf(10000)));
    }

    /**
     * Save purchase data
     * @param purchaseId PurchaseId ID
     * @param timestamp Purchase Time
     * @param waiting Wait time (in seconds) for points to be provided
     * @param totalAmount Total Purchase Amount
     * @param cacheAmount Amount purchased in cash
     * @param currency Currency symbol (case letter)
     * @param shopId Shop ID
     * @param userAccount User's wallet address
     * @param userPhone User's phone number
     * @param details Unit price and accumulated rate of purchased goods
     */
    public ResponseSavePurchase saveNewPurchase(
            String purchaseId,
            long timestamp,
            long waiting,
            String totalAmount,
            String cacheAmount,
            String currency,
            String shopId,
            @NotNull String userAccount,
            @NotNull String userPhone,
            PurchaseDetail[] details
    ) throws Exception  {
        String adjustedUserAccount = userAccount.trim().isEmpty() ? Address.DEFAULT.toString() : userAccount.trim();

        String adjustedUserPhone = userPhone.trim();
        if (!adjustedUserPhone.isEmpty()) {
            adjustedUserPhone = CommonUtils.getInternationalPhoneNumber(adjustedUserPhone);
        }

        SaveNewPurchase adjustedPurchase = new SaveNewPurchase(
                purchaseId,
                Amount.make(cacheAmount).getValue(),
                BigInteger.valueOf(0),
                currency,
                shopId,
                adjustedUserAccount,
                CommonUtils.getPhoneHash(adjustedUserPhone),
                this.assetAddress,
                ""
        );
        SaveNewOthers adjustedOthers = new SaveNewOthers(Amount.make(totalAmount).getValue(), timestamp, waiting);
        ArrayList<SaveNewDetail> saveDetails = new ArrayList<SaveNewDetail>();

        for (PurchaseDetail detail : details) {
            SaveNewDetail elem = new SaveNewDetail(detail.productId, Amount.make(detail.amount).getValue(), BigInteger.valueOf(detail.providePercent * 100));
            saveDetails.add(elem);
        }

        adjustedPurchase.loyalty = this.getLoyaltyInTransaction(adjustedPurchase.cashAmount, adjustedOthers.totalAmount, saveDetails);

        byte[] message = CommonUtils.getNewPurchaseDataMessage(
                adjustedPurchase.purchaseId,
                adjustedPurchase.cashAmount,
                adjustedPurchase.loyalty,
                adjustedPurchase.currency,
                adjustedPurchase.shopId,
                adjustedPurchase.userAccount,
                adjustedPurchase.userPhoneHash,
                adjustedPurchase.sender,
                this.getChainId()
        );
        adjustedPurchase.purchaseSignature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

        URI uri = new URI(String.format("%s/v2/tx/purchase/new", saveEndpoint));
        HttpURLConnection conn = getHttpURLConnection(uri, "POST");

        JSONObject body = new JSONObject();

        JSONObject purchaseObj = new JSONObject();
        purchaseObj.put("purchaseId", adjustedPurchase.purchaseId);
        purchaseObj.put("cashAmount", adjustedPurchase.cashAmount.toString());
        purchaseObj.put("loyalty", adjustedPurchase.loyalty.toString());
        purchaseObj.put("currency", adjustedPurchase.currency);
        purchaseObj.put("shopId", adjustedPurchase.shopId);
        purchaseObj.put("userAccount", adjustedPurchase.userAccount);
        purchaseObj.put("userPhoneHash", adjustedPurchase.userPhoneHash);
        purchaseObj.put("sender", adjustedPurchase.sender);
        purchaseObj.put("purchaseSignature", adjustedPurchase.purchaseSignature);
        body.put("purchase", purchaseObj);

        JSONObject othersObj = new JSONObject();
        othersObj.put("totalAmount", adjustedOthers.totalAmount.toString());
        othersObj.put("timestamp", String.valueOf(adjustedOthers.timestamp));
        othersObj.put("waiting", String.valueOf(adjustedOthers.waiting));
        body.put("others", othersObj);

        JSONArray detailsObj = new JSONArray();
        for (SaveNewDetail elem : saveDetails) {
            JSONObject elemObj = new JSONObject();
            elemObj.put("productId", elem.productId);
            elemObj.put("amount", elem.amount.toString());
            elemObj.put("providePercent", elem.providePercent.toString());
            detailsObj.put(elemObj);
        }
        body.put("details", detailsObj);

        try (OutputStream output = conn.getOutputStream()) {
            output.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        return ResponseSavePurchase.fromJSONObject(getJSONObjectResponse(conn));
    }

    /**
     * Cancellation process for payments that have already been completed
     * @param purchaseId PurchaseId ID
     * @param timestamp Purchase Time
     * @param waiting Wait time (in seconds) for points to be provided
     */
    public ResponseSavePurchase saveCancelPurchase(
            String purchaseId,
            long timestamp,
            long waiting
    ) throws Exception {
        SaveCancelPurchase adjustedPurchase = new SaveCancelPurchase(
                purchaseId,
                this.assetAddress,
                ""
        );
        byte[] message = CommonUtils.getCancelPurchaseDataMessage(
                adjustedPurchase.purchaseId,
                adjustedPurchase.sender,
                this.getChainId()
        );
        adjustedPurchase.purchaseSignature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);
        SaveCancelOthers adjustedOthers = new SaveCancelOthers(timestamp, waiting);

        URI uri = new URI(String.format("%s/v2/tx/purchase/cancel", saveEndpoint));
        HttpURLConnection conn = getHttpURLConnection(uri, "POST");

        JSONObject body = new JSONObject();

        JSONObject purchaseObj = new JSONObject();
        purchaseObj.put("purchaseId", adjustedPurchase.purchaseId);
        purchaseObj.put("sender", adjustedPurchase.sender);
        purchaseObj.put("purchaseSignature", adjustedPurchase.purchaseSignature);
        body.put("purchase", purchaseObj);

        JSONObject othersObj = new JSONObject();
        othersObj.put("timestamp", String.valueOf(adjustedOthers.timestamp));
        othersObj.put("waiting", String.valueOf(adjustedOthers.waiting));
        body.put("others", othersObj);

        try (OutputStream output = conn.getOutputStream()) {
            output.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        return ResponseSavePurchase.fromJSONObject(getJSONObjectResponse(conn));
    }
}
