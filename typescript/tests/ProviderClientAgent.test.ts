import { BOACoin, NetWorkType, ProviderClient } from "../src";

import * as assert from "assert";

describe("Test of ProviderClient", function () {
    this.timeout(1000 * 60 * 5);
    let agentClient: ProviderClient;
    const providerAddress = "0x64D111eA9763c93a003cef491941A011B8df5a49";
    const network: NetWorkType = NetWorkType.testnet;

    before(() => {
        agentClient = new ProviderClient(network, "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab");
    });

    it("Provider to Address by agent", async () => {
        const isProvider = await agentClient.isProvider(providerAddress);
        const receiver = "0xCf44157e9df307c90FC7762933Ac1e2921e8b39E";
        if (isProvider) {
            const res1 = await agentClient.getBalanceAccount(receiver);
            const oldBalance = res1.point.balance;

            const amount = BOACoin.make(200).value;
            await agentClient.provideToAddress(providerAddress, receiver, amount);

            const res2 = await agentClient.getBalanceAccount(receiver);
            assert.deepStrictEqual(res2.point.balance, oldBalance.add(amount));
        } else {
            console.log("Not Provider");
        }
    });

    it("Provider to Phone by agent", async () => {
        const isProvider = await agentClient.isProvider(providerAddress);
        const phoneNumber = "+82 10-9000-5000";
        if (isProvider) {
            const res1 = await agentClient.getBalancePhone(phoneNumber);
            const oldBalance = res1.point.balance;

            const amount = BOACoin.make(100).value;
            await agentClient.provideToPhone(providerAddress, phoneNumber, amount);

            const res2 = await agentClient.getBalancePhone(phoneNumber);
            assert.deepStrictEqual(res2.point.balance, oldBalance.add(amount));
        } else {
            console.log("Not Provider");
        }
    });
});
