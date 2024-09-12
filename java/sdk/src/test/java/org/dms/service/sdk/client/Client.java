package org.dms.service.sdk.client;

import org.dms.service.sdk.data.NetWorkType;
import org.dms.service.sdk.data.UserBalance;
import org.dms.service.sdk.utils.CommonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTest {
    @Test void getChainId() {
        Client client = new Client(NetWorkType.testnet);
        try {
            long value = client.getChainId();
            assertEquals(value, 215115);
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
    @Test void getBalancePhone() {
        Client client = new Client(NetWorkType.testnet);
        try {
            UserBalance balance = client.getBalancePhone("+82 10-1000-2099");
            assertEquals(balance.point.balance.toString(), "5000000000000000000000000");
            assertEquals(balance.token.balance.toString(), "0");
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
    @Test void getBalancePhoneHash() {
        Client client = new Client(NetWorkType.testnet);
        try {
            UserBalance balance = client.getBalancePhoneHash("0x6e2f492102956a83a350152070be450b44fa19c08455c74b3aa79cc74195d3ba");
            assertEquals(balance.point.balance.toString(), "5000000000000000000000000");
            assertEquals(balance.token.balance.toString(), "0");
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
    @Test void getBalanceAccount() {
        Client client = new Client(NetWorkType.testnet);
        try {
            UserBalance balance = client.getBalanceAccount("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563");
            assertEquals(balance.point.balance.toString(), "5000000000000000000000000");
            assertEquals(balance.token.balance.toString(), "100000000000000000000000");
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
    @Test void getLedgerNonceOf() {
        Client client = new Client(NetWorkType.testnet);
        try {
            long nonce = client.getLedgerNonceOf("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563");
            assertTrue(nonce >= 0);
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
    @Test void getInternationalPhoneNumber() {
        Client client = new Client(NetWorkType.testnet);
        try {
            String res = CommonUtils.getInternationalPhoneNumber("+82 010 1000 2099");
            assertEquals(res, "+82 10-1000-2099");
        } catch (Exception e) {
            Assertions.assertEquals("some exception message...", e.getMessage());
        }
    }
}
