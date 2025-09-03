package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 18/03/2019.
 */

@Entity
@Table(name = "sys_brk_life_rcts")
public class LifeReceipts {
    @Id
    @SequenceGenerator(name = "LifeReceiptSeq",sequenceName = "life_receipt_seq",allocationSize=1)
    @GeneratedValue(generator = "LifeReceiptSeq")
    @Column(name="lrct_id")
    private Long receiptId;

    @Column(name="receipt_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date receiptDate;

    @Column(name="receipt_value_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date receiptValueDate;

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lrct_policy_id")
    private PolicyTrans policyTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="lrct_receipt_id")
    private ReceiptTrans receiptTrans;

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lrct_trans_no")
    private SystemTransactions systemTransaction;



    @Column(name="lrct_amt")
    private BigDecimal receiptAmt;

    @Column(name="lrct_dc")
    private String drCr;

    @Column(name="lrct_alloc_amt")
    private BigDecimal allocatedAmt;

    @Column(name="lrct_balance")
    private BigDecimal balanceAmt;

    @Column(name="lrct_taxes")
    private Long taxeAmt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lrct_lrct_id")
    private LifeReceipts originalLifeReceipts;

    @Column(name="lrct_done_date")
    private Date doneDate;

    @ManyToOne
    @JoinColumn(name="lrct_done_by",nullable=false )
    private User doneBy;


    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public Date getReceiptValueDate() {
        return receiptValueDate;
    }

    public void setReceiptValueDate(Date receiptValueDate) {
        this.receiptValueDate = receiptValueDate;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public ReceiptTrans getReceiptTrans() {
        return receiptTrans;
    }

    public void setReceiptTrans(ReceiptTrans receiptTrans) {
        this.receiptTrans = receiptTrans;
    }

    public BigDecimal getReceiptAmt() {
        return receiptAmt;
    }

    public void setReceiptAmt(BigDecimal receiptAmt) {
        this.receiptAmt = receiptAmt;
    }

    public BigDecimal getAllocatedAmt() {
        return allocatedAmt;
    }

    public void setAllocatedAmt(BigDecimal allocatedAmt) {
        this.allocatedAmt = allocatedAmt;
    }

    public BigDecimal getBalanceAmt() {
        return balanceAmt;
    }

    public void setBalanceAmt(BigDecimal balanceAmt) {
        this.balanceAmt = balanceAmt;
    }

    public Long getTaxeAmt() {
        return taxeAmt;
    }

    public void setTaxeAmt(Long taxeAmt) {
        this.taxeAmt = taxeAmt;
    }

    public String getDrCr() {
        return drCr;
    }

    public void setDrCr(String drCr) {
        this.drCr = drCr;
    }

    public User getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(User doneBy) {
        this.doneBy = doneBy;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public SystemTransactions getSystemTransaction() {
        return systemTransaction;
    }

    public void setSystemTransaction(SystemTransactions systemTransaction) {
        this.systemTransaction = systemTransaction;
    }

    public LifeReceipts getOriginalLifeReceipts() {
        return originalLifeReceipts;
    }

    public void setOriginalLifeReceipts(LifeReceipts originalLifeReceipts) {
        this.originalLifeReceipts = originalLifeReceipts;
    }
}
