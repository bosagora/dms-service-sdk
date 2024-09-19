import { BigNumber } from "@ethersproject/bignumber";

/**
 * Network Type
 */
export enum NetWorkType {
    testnet,
    mainnet,
    localhost,
}

/**
 * Information of endpoints
 */
export interface IEndpoints {
    relay: string;
    save: string;
}

/**
 * Data with balance saved
 */
export interface IBalance {
    balance: BigNumber;
    value: BigNumber;
}

/**
 * Balance data for points and tokens
 */
export interface IUserBalance {
    point: IBalance;
    token: IBalance;
}

/**
 * Response data for the estimated payment amount of the product
 */
export interface IPaymentInfo {
    /**
     * Wallet address
     */
    account: string;
    /**
     * Purchase amount
     */
    amount: BigNumber;
    /**
     * Currency symbol
     */
    currency: string;
    /**
     * Balance of point
     */
    balance: BigNumber;
    /**
     * Balance converted in currency units
     */
    balanceValue: BigNumber;
    /**
     * Points to be paid
     */
    paidPoint: BigNumber;
    /**
     * Amount of points to be paid converted into currency units
     */
    paidValue: BigNumber;
    /**
     * Points to be paid as fees
     */
    feePoint: BigNumber;
    /**
     * Amount of points to be paid as fees converted into currency units
     */
    feeValue: BigNumber;
    /**
     * the sum of the fees and the points to be paid
     */
    totalPoint: BigNumber;
    /**
     * the sum of fees and points to be paid in currency units
     */
    totalValue: BigNumber;
}

/**
 * Data generated during the payment process
 */
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

/**
 * Reduction of data generated during the payment process
 */
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

/**
 * Data generated during store information modification
 */
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

/**
 * Details of the purchase
 */
export interface IPurchaseDetail {
    productId: string;
    amount: number;
    providePercent: number;
}
