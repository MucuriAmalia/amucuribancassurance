package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.setup.model.User;

import javax.persistence.*;

/**
 * Created by HP on 3/24/2018.
 */
@Entity
@Table(name="sys_brk_receipt_print")
public class ReceiptPrint {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rep_id")
    private Long cerpId;

    @ManyToOne
    @JoinColumn(name="rep_user_code",nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="rep_rct_id",nullable=false,unique = true)
    private ReceiptTrans receiptTrans;


    public Long getCerpId() {
        return cerpId;
    }

    public void setCerpId(Long cerpId) {
        this.cerpId = cerpId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReceiptTrans getReceiptTrans() {
        return receiptTrans;
    }

    public void setReceiptTrans(ReceiptTrans receiptTrans) {
        this.receiptTrans = receiptTrans;
    }


    @Override
    public String toString() {
        return "ReceiptPrint{" +
                "cerpId=" + cerpId +
                '}';
    }
}
