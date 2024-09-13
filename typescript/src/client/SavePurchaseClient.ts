import { HTTPClient } from "../network/HTTPClient";
import { IPurchaseDetail, NetWorkType } from "../types";
import { BOACoin } from "../utils/Amount";
import { CommonUtils } from "../utils/CommonUtils";
import { Client } from "./Client";

import { BigNumber } from "@ethersproject/bignumber";
import { AddressZero } from "@ethersproject/constants";
import { Wallet } from "@ethersproject/wallet";

import { PhoneNumberFormat, PhoneNumberUtil } from "google-libphonenumber";

import URI from "urijs";

/**
 * The client that delivers purchase data to the loyalty system to store purchase data in IPFS.
 * First, you must obtain permission from the loyalty system and register the address of the wallet to be used in the loyalty system.
 */
export class SavePurchaseClient extends Client {
    /**
     * Message repeater's wallet for saving purchases
     * @private
     */
    private readonly wallet: Wallet;

    /**
     * wallet address of asset owner
     * @private
     */
    private readonly assetAddress: string;

    /**
     * Constructor
     * @param network Type of network (mainnet, testnet, localhost)
     * @param privateKey The private key used in the saving purchases
     * @param assetAddress The wallet address of asset owner
     */
    constructor(network: NetWorkType, privateKey: string, assetAddress: string) {
        super(network);
        this.wallet = new Wallet(privateKey);
        this.assetAddress = assetAddress;
    }

    public get address(): string {
        return this.wallet.address;
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
    public async saveNewPurchase(
        purchaseId: string,
        timestamp: bigint,
        waiting: bigint,
        totalAmount: number,
        cacheAmount: number,
        currency: string,
        shopId: string,
        userAccount: string,
        userPhone: string,
        details: IPurchaseDetail[]
    ): Promise<void> {
        let adjustedUserAccount = userAccount.trim();
        if (adjustedUserAccount !== "") {
            const eth = /^(0x)[0-9a-f]{40}$/i;
            if (!eth.test(adjustedUserAccount)) {
                throw new Error(`This is not a wallet address ${userAccount}`);
            }
        } else {
            adjustedUserAccount = AddressZero;
        }

        const phoneUtil = PhoneNumberUtil.getInstance();
        let adjustedUserPhone = userPhone.trim();
        try {
            if (adjustedUserPhone !== "") {
                const number = phoneUtil.parseAndKeepRawInput(userPhone, "ZZ");
                if (!phoneUtil.isValidNumber(number)) {
                    throw new Error(`This is not a phone number format ${adjustedUserPhone}`);
                } else {
                    adjustedUserPhone = phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL);
                }
            }
        } catch (error) {
            throw new Error(`This is not a phone number format ${adjustedUserPhone}`);
        }

        const adjustedPurchase: ISavePurchase = {
            purchaseId,
            cashAmount: BOACoin.make(cacheAmount).value,
            loyalty: BigNumber.from(0),
            currency,
            shopId,
            userAccount: adjustedUserAccount,
            userPhoneHash: CommonUtils.getPhoneHash(adjustedUserPhone),
            sender: this.assetAddress,
            purchaseSignature: "",
        };

        const adjustedOthers: ISaveOthers = {
            totalAmount: BOACoin.make(totalAmount).value,
            timestamp,
            waiting,
        };

        const adjustedDetails: ISaveDetail[] = details.map((elem) => {
            return {
                productId: elem.productId,
                amount: BOACoin.make(elem.amount).value,
                providePercent: BigNumber.from(Math.floor(Number(elem.providePercent) * 100)),
            };
        });

        adjustedPurchase.loyalty = this.getLoyaltyInTransaction(
            adjustedPurchase.cashAmount,
            adjustedOthers.totalAmount,
            adjustedDetails
        );

        const message = CommonUtils.getNewPurchaseDataMessage(
            adjustedPurchase.purchaseId,
            adjustedPurchase.cashAmount,
            adjustedPurchase.loyalty,
            adjustedPurchase.currency,
            adjustedPurchase.shopId,
            adjustedPurchase.userAccount,
            adjustedPurchase.userPhoneHash,
            adjustedPurchase.sender,
            await this.getChainId()
        );
        adjustedPurchase.purchaseSignature = await CommonUtils.signMessage(this.wallet, message);

        const body = {
            purchase: adjustedPurchase,
            others: adjustedOthers,
            details: adjustedDetails,
        };

        const client = new HTTPClient({});

        const response = await client.post(URI(this.endpoints.save).directory("/v2/tx/purchase/new").toString(), body);
        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return response.data.data;
    }

    /**
     * Cancellation process for payments that have already been completed
     * @param purchaseId PurchaseId ID
     * @param timestamp Purchase Time
     * @param waiting Wait time (in seconds) for points to be provided
     */
    public async saveCancelPurchase(purchaseId: string, timestamp: bigint, waiting: bigint) {
        const adjustedPurchase = {
            purchaseId,
            sender: this.assetAddress,
            purchaseSignature: "",
        };
        const message = CommonUtils.getCancelPurchaseDataMessage(
            adjustedPurchase.purchaseId,
            adjustedPurchase.sender,
            await this.getChainId()
        );
        adjustedPurchase.purchaseSignature = await CommonUtils.signMessage(this.wallet, message);

        const adjustedOthers = {
            timestamp,
            waiting,
        };
        const body = {
            purchase: adjustedPurchase,
            others: adjustedOthers,
        };

        const client = new HTTPClient({});

        const response = await client.post(
            URI(this.endpoints.save).directory("/v2/tx/purchase/cancel").toString(),
            body
        );
        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return response.data.data;
    }

    private getLoyaltyInTransaction(cashAmount: BigNumber, totalAmount: BigNumber, details: ISaveDetail[]): BigNumber {
        if (totalAmount.eq(0)) return BigNumber.from(0);
        if (cashAmount.eq(0)) return BigNumber.from(0);
        let sum: BigNumber = BigNumber.from(0);
        for (const elem of details) {
            sum = sum.add(elem.amount.mul(elem.providePercent));
        }
        return CommonUtils.zeroGWEI(sum.mul(cashAmount).div(totalAmount).div(10000));
    }
}

interface ISavePurchase {
    purchaseId: string;
    cashAmount: BigNumber;
    loyalty: BigNumber;
    currency: string;
    shopId: string;
    userAccount: string;
    userPhoneHash: string;
    sender: string;
    purchaseSignature: string;
}

interface ISaveOthers {
    totalAmount: BigNumber;
    timestamp: bigint;
    waiting: bigint;
}

interface ISaveDetail {
    productId: string;
    amount: BigNumber;
    providePercent: BigNumber;
}
