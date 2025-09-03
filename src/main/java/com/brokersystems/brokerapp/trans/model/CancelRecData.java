package com.brokersystems.brokerapp.trans.model;

import java.util.List;

public class CancelRecData {

    private List<CancelData> receipts;
    private boolean isApproved;

    public List<CancelData> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<CancelData> receipts) {
        this.receipts = receipts;
    }
    public boolean isApproved() {
        return isApproved;
    }
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

}
