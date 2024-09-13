import { HTTPClient } from "../network/HTTPClient";
import { IEndpoints, IUserBalance, NetWorkType } from "../types";

import URI from "urijs";

import { BigNumber } from "@ethersproject/bignumber";

/**
 * The client class of decentralized loyalty services
 */
export class Client {
    /**
     * Endpoint of API
     * @protected
     */
    protected endpoints: IEndpoints;

    /**
     * Chain ID of side chain
     * @private
     */
    private chainId: number = 0;

    /**
     * Constructor
     * @param network Type of network (mainnet, testnet, localhost)
     */
    constructor(network: NetWorkType) {
        if (network === NetWorkType.localhost) {
            this.endpoints = {
                relay: "http://127.0.0.1:7070",
                save: "http://127.0.0.1:3030",
            };
        } else if (network === NetWorkType.mainnet) {
            this.endpoints = {
                relay: "https://relay.main.acccoin.io",
                save: "https://save.main.acccoin.io",
            };
        } else {
            this.endpoints = {
                relay: "https://relay.test.acccoin.io",
                save: "https://save.test.acccoin.io",
            };
        }
    }

    /**
     * Provide the ID of the chain
     */
    public async getChainId(): Promise<number> {
        if (this.chainId === 0) {
            const agent = new HTTPClient({});
            const response = await agent.get(URI(this.endpoints.relay).directory("/v1/chain/side/id").toString());

            if (response.data.code !== 0) {
                throw new Error(response.data.error?.message);
            }

            this.chainId = Number(response.data.data.chainId);
        }
        return this.chainId;
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneNumber User's phone number
     */
    public async getBalancePhone(phoneNumber: string): Promise<IUserBalance> {
        const agent = new HTTPClient({});
        const response = await agent.get(
            URI(this.endpoints.relay).directory("/v1/ledger/balance/phone/").filename(phoneNumber).toString()
        );

        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return {
            point: {
                balance: BigNumber.from(response.data.data.point.balance),
                value: BigNumber.from(response.data.data.point.value),
            },
            token: {
                balance: BigNumber.from(response.data.data.token.balance),
                value: BigNumber.from(response.data.data.token.value),
            },
        };
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneHash User's phone number hash
     */
    public async getBalancePhoneHash(phoneHash: string): Promise<IUserBalance> {
        const agent = new HTTPClient({});
        const response = await agent.get(
            URI(this.endpoints.relay).directory("/v1/ledger/balance/phoneHash/").filename(phoneHash).toString()
        );

        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return {
            point: {
                balance: BigNumber.from(response.data.data.point.balance),
                value: BigNumber.from(response.data.data.point.value),
            },
            token: {
                balance: BigNumber.from(response.data.data.token.balance),
                value: BigNumber.from(response.data.data.token.value),
            },
        };
    }

    /**
     * Provide the user's points and token balance information
     * @param account User's wallet address
     */
    public async getBalanceAccount(account: string): Promise<IUserBalance> {
        const agent = new HTTPClient({});
        const response = await agent.get(
            URI(this.endpoints.relay).directory("/v1/ledger/balance/account/").filename(account).toString()
        );

        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }

        return {
            point: {
                balance: BigNumber.from(response.data.data.point.balance),
                value: BigNumber.from(response.data.data.point.value),
            },
            token: {
                balance: BigNumber.from(response.data.data.token.balance),
                value: BigNumber.from(response.data.data.token.value),
            },
        };
    }

    /**
     * Provide a nonce corresponding to the user's wallet address. It provides a nonce corresponding to the user's wallet address.
     * This ensures that the same signature is not repeated. And this value is recorded in Contract and automatically increases by 1.
     * @param account User's wallet address
     */
    public async getLedgerNonceOf(account: string): Promise<number> {
        const agent = new HTTPClient({});
        const response = await agent.get(
            URI(this.endpoints.relay).directory("/v1/ledger/nonce/").filename(account).toString()
        );
        if (response.data.code !== 0) {
            throw new Error(response.data.error?.message);
        }
        return Number(response.data.data.nonce);
    }
}
