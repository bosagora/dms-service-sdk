import { HTTPClient } from "../network/HTTPClient";
import { IPaymentInfo, IPaymentTaskItem, ITaskItemCallback, NetWorkType } from "../types";
import { CommonUtils } from "../utils/CommonUtils";
import { Client } from "./Client";

import { BigNumber } from "@ethersproject/bignumber";
import { Wallet } from "@ethersproject/wallet";

import URI from "urijs";

export class PaymentClient extends Client {
    /**
     * Message repeater's wallet for payment
     * @private
     */
    private readonly wallet: Wallet;

    constructor(network: NetWorkType, privateKey: string) {
        super(network);
        this.wallet = new Wallet(privateKey);
    }

    public get address(): string {
        return this.wallet.address;
    }

    /**
     * It calculates the amount required for payment.
     * @param account   User's wallet address or temporary address
     * @param amount    Purchase amount
     * @param currency  Currency symbol (case letter)
     */
    public async getPaymentInfo(account: string, amount: BigNumber, currency: string): Promise<IPaymentInfo> {
        const client = new HTTPClient({});
        const response = await client.get(
            URI(this.endpoints.relay)
                .directory("/v2/payment/info")
                .addQuery("account", account)
                .addQuery("amount", amount.toString())
                .addQuery("currency", currency)
                .toString()
        );
        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return {
            account: response.data.data.account,
            amount: BigNumber.from(response.data.data.amount),
            currency: response.data.data.currency,
            balance: BigNumber.from(response.data.data.balance),
            balanceValue: BigNumber.from(response.data.data.balanceValue),
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
        };
    }

    /**
     * Start a new payment
     * @param purchaseId    Purchase ID
     * @param account       User's wallet address or temporary address
     * @param amount        Purchase amount
     * @param currency      Currency symbol (case letter)
     * @param shopId        Shop ID
     * @param terminalId    Terminal ID
     */
    public async openNewPayment(
        purchaseId: string,
        account: string,
        amount: BigNumber,
        currency: string,
        shopId: string,
        terminalId: string
    ): Promise<IPaymentTaskItem> {
        const client = new HTTPClient();
        const message = CommonUtils.getOpenNewPaymentMessage(purchaseId, amount, currency, shopId, account, terminalId);
        const signature = await CommonUtils.signMessage(this.wallet, message);
        const response = await client.post(
            URI(this.endpoints.relay).directory("/v2/payment/new").filename("open").toString(),
            {
                purchaseId,
                amount: amount.toString(),
                currency,
                shopId,
                account,
                terminalId,
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
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }

    /**
     * Close the new payment
     * @param paymentId Payment ID
     * @param confirm If this value is true, the payment will be terminated normally, otherwise the payment will be canceled.
     */
    public async closeNewPayment(paymentId: string, confirm: boolean): Promise<IPaymentTaskItem> {
        const client = new HTTPClient();
        const message = CommonUtils.getCloseNewPaymentMessage(paymentId, confirm);
        const signature = await CommonUtils.signMessage(this.wallet, message);
        const response = await client.post(
            URI(this.endpoints.relay).directory("/v2/payment/new").filename("close").toString(),
            {
                paymentId,
                confirm,
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
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }

    /**
     * Start processing cancellation of previously completed new payments
     * @param paymentId  Payment ID
     * @param terminalId Terminal ID
     */
    public async openCancelPayment(paymentId: string, terminalId: string): Promise<IPaymentTaskItem> {
        const client = new HTTPClient();
        const message = CommonUtils.getOpenCancelPaymentMessage(paymentId, terminalId);
        const signature = await CommonUtils.signMessage(this.wallet, message);
        const response = await client.post(
            URI(this.endpoints.relay).directory("/v2/payment/cancel").filename("open").toString(),
            {
                paymentId,
                terminalId,
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
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }

    /**
     * Close the cancellation payment
     * @param paymentId Payment ID
     * @param confirm If this value is true, the payment will be terminated normally, otherwise the payment will be canceled.
     */
    public async closeCancelPayment(paymentId: string, confirm: boolean): Promise<IPaymentTaskItem> {
        const client = new HTTPClient();
        const message = CommonUtils.getCloseCancelPaymentMessage(paymentId, confirm);
        const signature = await CommonUtils.signMessage(this.wallet, message);
        const response = await client.post(
            URI(this.endpoints.relay).directory("/v2/payment/cancel").filename("close").toString(),
            {
                paymentId,
                confirm,
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
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }

    /**
     * Provide detailed information on the payment
     * @param paymentId Payment ID
     */
    public async getPaymentItem(paymentId: string): Promise<IPaymentTaskItem> {
        const client = new HTTPClient({});
        const response = await client.get(
            URI(this.endpoints.relay).directory("/v2/payment/item").addQuery("paymentId", paymentId).toString()
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
            paidPoint: BigNumber.from(response.data.data.paidPoint),
            paidValue: BigNumber.from(response.data.data.paidValue),
            feePoint: BigNumber.from(response.data.data.feePoint),
            feeValue: BigNumber.from(response.data.data.feeValue),
            totalPoint: BigNumber.from(response.data.data.totalPoint),
            totalValue: BigNumber.from(response.data.data.totalValue),
            terminalId: response.data.data.terminalId,
            paymentStatus: Number(response.data.data.paymentStatus),
        };
    }

    public async getLatestTaskSequence(): Promise<bigint> {
        const client = new HTTPClient({});
        const response = await client.get(
            URI(this.endpoints.relay).directory("/v1/task/sequence").filename("latest").toString()
        );

        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return BigInt(response.data.data.sequence);
    }

    public async getTasks(sequence: bigint): Promise<ITaskItemCallback[]> {
        const client = new HTTPClient({});
        const response = await client.get(
            URI(this.endpoints.relay).directory("/v1/task/list").filename(sequence.toString()).toString()
        );

        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        const res: ITaskItemCallback[] = [];
        for (const item of response.data.data) {
            if (item.type === "pay_new" || item.type === "pay_cancel") {
                res.push({
                    sequence: BigInt(item.sequence),
                    type: item.type,
                    code: item.code,
                    message: item.message,
                    data: {
                        paymentId: item.data.paymentId,
                        purchaseId: item.data.purchaseId,
                        amount: BigNumber.from(item.data.amount),
                        currency: item.data.currency,
                        shopId: item.data.shopId,
                        account: item.data.account,
                        paidPoint: BigNumber.from(item.data.paidPoint),
                        paidValue: BigNumber.from(item.data.paidValue),
                        feePoint: BigNumber.from(item.data.feePoint),
                        feeValue: BigNumber.from(item.data.feeValue),
                        totalPoint: BigNumber.from(item.data.totalPoint),
                        totalValue: BigNumber.from(item.data.totalValue),
                        terminalId: item.data.terminalId,
                        paymentStatus: item.data.paymentStatus,
                    },
                });
            } else {
                res.push({
                    sequence: BigInt(item.sequence),
                    type: item.type,
                    code: item.code,
                    message: item.message,
                    data: {
                        taskId: item.data.taskId,
                        shopId: item.data.shopId,
                        name: item.data.name,
                        currency: item.data.currency,
                        status: item.data.status,
                        account: item.data.account,
                        terminalId: item.data.terminalId,
                        taskStatus: item.data.taskStatus,
                    },
                });
            }
        }

        return res;
    }
}
