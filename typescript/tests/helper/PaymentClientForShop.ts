import { Client } from "../../src/client/Client";
import { HTTPClient } from "../../src/network/HTTPClient";
import { IPaymentTaskItemShort, NetWorkType } from "../../src/types";

import { BigNumber, BigNumberish } from "@ethersproject/bignumber";
import { Wallet } from "@ethersproject/wallet";

// @ts-ignore
import URI from "urijs";

import { defaultAbiCoder } from "@ethersproject/abi";
import { Signer } from "@ethersproject/abstract-signer";
import { arrayify, BytesLike } from "@ethersproject/bytes";
import { keccak256 } from "@ethersproject/keccak256";

export class PaymentClientForShop extends Client {
    private readonly shopWallet: Wallet;
    private readonly shopId: string;

    constructor(network: NetWorkType, privateKey: string, shopId: string) {
        super(network);
        this.shopWallet = new Wallet(privateKey);
        this.shopId = shopId;
    }

    public get address(): string {
        return this.shopWallet.address;
    }

    public static getLoyaltyCancelPaymentMessage(
        address: string,
        paymentId: BytesLike,
        purchaseId: string,
        nonce: BigNumberish,
        chainId: BigNumberish
    ): Uint8Array {
        const encodedResult = defaultAbiCoder.encode(
            ["bytes32", "string", "address", "uint256", "uint256"],
            [paymentId, purchaseId, address, chainId, nonce]
        );
        return arrayify(keccak256(encodedResult));
    }

    public static async signLoyaltyCancelPayment(
        signer: Signer,
        paymentId: BytesLike,
        purchaseId: string,
        nonce: BigNumberish,
        chainId: BigNumberish
    ): Promise<string> {
        const message = PaymentClientForShop.getLoyaltyCancelPaymentMessage(
            await signer.getAddress(),
            paymentId,
            purchaseId,
            nonce,
            chainId
        );
        return signer.signMessage(message);
    }

    public getShopId(): string {
        return this.shopId;
    }

    public async approveCancelPayment(
        paymentId: string,
        purchaseId: string,
        approval: boolean
    ): Promise<IPaymentTaskItemShort> {
        const account: string = this.shopWallet.address;
        const nonce = await this.getLedgerNonceOf(account);
        const signature = await PaymentClientForShop.signLoyaltyCancelPayment(
            this.shopWallet,
            paymentId,
            purchaseId,
            nonce,
            await this.getChainId()
        );
        const client = new HTTPClient({});
        const response = await client.post(
            URI(this.endpoints.relay).directory("/v1/payment/cancel/approval").toString(),
            {
                paymentId,
                approval,
                signature,
            }
        );
        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }
        return {
            paymentId: response.data.data.paymentId,
            purchaseId: response.data.data.purchaseId,
            amount: BigNumber.from(response.data.data.amount),
            currency: response.data.data.currency,
            shopId: response.data.data.shopId,
            account: response.data.data.account,
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }
}
