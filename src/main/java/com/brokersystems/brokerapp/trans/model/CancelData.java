package com.brokersystems.brokerapp.trans.model;

import java.util.List;

public class CancelData {

     private Long receiptId;
     private String commentl;

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public String getCommentl() {
        return commentl;
    }

    public void setCommentl(String commentl) {
        this.commentl = commentl;
    }
}
