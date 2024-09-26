package org.acc.service.sdk.data.purchase;

public class SaveCancelOthers {
    public long timestamp;
    public long waiting;

    public SaveCancelOthers(long timestamp, long waiting) {
        this.timestamp = timestamp;
        this.waiting = waiting;
    }
}
