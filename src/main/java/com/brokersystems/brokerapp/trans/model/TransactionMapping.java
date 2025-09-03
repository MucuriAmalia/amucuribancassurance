package com.brokersystems.brokerapp.trans.model;

import javax.persistence.*;

/**
 * Created by peter on 4/24/2017.
 */
@Entity
@Table(name="sys_brk_trans_mapping")
public class TransactionMapping {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="tm_no")
    private Long tmNo;

    @Column(name="tm_trans_type")
    private String transType;

    @Column(name="tm_dr_code")
    private String debitCode;

    @Column(name="tm_cr_code")
    private String creditCode;

    public Long getTmNo() {
        return tmNo;
    }

    public void setTmNo(Long tmNo) {
        this.tmNo = tmNo;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getDebitCode() {
        return debitCode;
    }

    public void setDebitCode(String debitCode) {
        this.debitCode = debitCode;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }
}
