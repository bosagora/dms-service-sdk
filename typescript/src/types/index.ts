import { BigNumber } from "@ethersproject/bignumber";

export enum NetWorkType {
    testnet,
    mainnet,
    localhost,
}

export interface IEndpoints {
    relay: string;
    save: string;
}

export interface IBalance {
    balance: BigNumber;
    value: BigNumber;
}

export interface IUserBalance {
    point: IBalance;
    token: IBalance;
}

export interface IPaymentInfo {
    account: string;
    amount: BigNumber;
    currency: string;
    balance: BigNumber;
    balanceValue: BigNumber;
    paidPoint: BigNumber;
    paidValue: BigNumber;
    feePoint: BigNumber;
    feeValue: BigNumber;
    totalPoint: BigNumber;
    totalValue: BigNumber;
}

export interface IPaymentTaskItem {
    paymentId: string;
    purchaseId: string;
    amount: BigNumber;
    currency: string;
    shopId: string;
    account: string;
    paidPoint: BigNumber;
    paidValue: BigNumber;
    feePoint: BigNumber;
    feeValue: BigNumber;
    totalPoint: BigNumber;
    totalValue: BigNumber;
    terminalId: string;
    paymentStatus: number;
}

export interface IPaymentTaskItemShort {
    paymentId: string;
    purchaseId: string;
    amount: BigNumber;
    currency: string;
    shopId: string;
    account: string;
    terminalId: string;
    paymentStatus: number;
}

export interface IShopTaskItem {
    taskId: string;
    shopId: string;
    name: string;
    currency: string;
    status: number;
    account: string;
    terminalId: string;
    taskStatus: number;
}

export interface ITaskItemCallback {
    sequence: bigint;
    type: string;
    code: number;
    message: string;
    data: IPaymentTaskItem | IShopTaskItem;
}

export interface IRawSavePurchaseDetail {
    productId: string;
    amount: number;
    providePercent: number;
}

export interface ISavePurchase {
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

export interface ISaveOthers {
    totalAmount: BigNumber;
    timestamp: bigint;
    waiting: bigint;
}

export interface ISaveDetail {
    productId: string;
    amount: BigNumber;
    providePercent: BigNumber;
}
