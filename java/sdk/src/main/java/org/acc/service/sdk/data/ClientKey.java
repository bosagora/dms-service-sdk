package org.acc.service.sdk.data;

public class ClientKey {
    public String address;
    public String privateKey;
    public ClientKey(String address, String privateKey) {
        this.address = address;
        this.privateKey = privateKey;
    }
}
