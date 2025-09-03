package com.brokersystems.brokerapp.life.model;


import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.SystemTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 18/03/2019.
 */
@Entity
@Table(name = "sys_brk_life_rct_allocs")
public class LifeReceiptAllocations {

    @Id
    @SequenceGenerator(name = "ReceiptAllocSeq",sequenceName = "receipt_alloc_seq",allocationSize=1)
    @GeneratedValue(generator = "ReceiptAllocSeq")
    @Column(name="alloc_id")
    private Long allocId;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="alloc_lrct_id")
    private LifeReceipts lifeReceipts;

    @ManyToOne
    @JoinColumn(name="alloc_tran_no")
    private SystemTrans transaction;


    @Column(name="alloc_install_no")
    private Integer installNo;


    @Column(name="alloc_date")
    private Date allocDate;

    @Column(name="alloc_paid_to_date")
    private Date paidToDate;


    @Column(name="alloc_instlmnt_amt")
    private BigDecimal instalmentPremium;

    @Column(name="alloc_comm_amt")
    private BigDecimal commAmount;

    @Column(name="alloc_subagent_comm_amt")
    private BigDecimal subAgentCommAmount;

    @Column(name="alloc_marketer_comm_amt")
    private BigDecimal marketerCommAmount;

    @Column(name="alloc_admin_fee_amt")
    private BigDecimal adminFeeAmt;

    @Column(name="alloc_amt")
    private BigDecimal allocAmount;

    @Column(name="alloc_ref_no")
    private String refNo;


    @ManyToOne
    @JoinColumn(name="alloc_done_by",nullable=false)
    private User doneBy;

    @Column(name="alloc_done_date")
    private Date doneDate;

    @Column(name="alloc_life_whtx_amt")
    private BigDecimal lifeWhtx;

    @Column(name="alloc_admin_fee_whtx_amt")
    private BigDecimal adminFeeAmtWhtx;

    public BigDecimal getAdminFeeAmtWhtx() { return adminFeeAmtWhtx;}

    public void setAdminFeeAmtWhtx(BigDecimal adminFeeAmtWhtx) {this.adminFeeAmtWhtx = adminFeeAmtWhtx;}

    public BigDecimal getLifeWhtx() {
        return lifeWhtx;
    }

    public void setLifeWhtx(BigDecimal lifeWhtx) {
        this.lifeWhtx = lifeWhtx;
    }

    public BigDecimal getMarketerCommAmount() {
        return marketerCommAmount;
    }

    public void setMarketerCommAmount(BigDecimal marketerCommAmount) {
        this.marketerCommAmount = marketerCommAmount;
    }

    public BigDecimal getAdminFeeAmt() {
        return adminFeeAmt;
    }

    public void setAdminFeeAmt(BigDecimal adminFeeAmt) {
        this.adminFeeAmt = adminFeeAmt;
    }

    public BigDecimal getSubAgentCommAmount() {
        return subAgentCommAmount;
    }

    public void setSubAgentCommAmount(BigDecimal subAgentCommAmount) {
        this.subAgentCommAmount = subAgentCommAmount;
    }

    public Long getAllocId() {
        return allocId;
    }

    public void setAllocId(Long allocId) {
        this.allocId = allocId;
    }

    public LifeReceipts getLifeReceipts() {
        return lifeReceipts;
    }

    public void setLifeReceipts(LifeReceipts lifeReceipts) {
        this.lifeReceipts = lifeReceipts;
    }

    public Integer getInstallNo() {
        return installNo;
    }

    public void setInstallNo(Integer installNo) {
        this.installNo = installNo;
    }

    public Date getAllocDate() {
        return allocDate;
    }

    public void setAllocDate(Date allocDate) {
        this.allocDate = allocDate;
    }

    public Date getPaidToDate() {
        return paidToDate;
    }

    public void setPaidToDate(Date paidToDate) {
        this.paidToDate = paidToDate;
    }

    public BigDecimal getInstalmentPremium() {
        return instalmentPremium;
    }

    public void setInstalmentPremium(BigDecimal instalmentPremium) {
        this.instalmentPremium = instalmentPremium;
    }

    public BigDecimal getAllocAmount() {
        return allocAmount;
    }

    public void setAllocAmount(BigDecimal allocAmount) {
        this.allocAmount = allocAmount;
    }

    public BigDecimal getCommAmount() {
        return commAmount;
    }

    public void setCommAmount(BigDecimal commAmount) {
        this.commAmount = commAmount;
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

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public SystemTrans getTransaction() {
        return transaction;
    }

    public void setTransaction(SystemTrans transaction) {
        this.transaction = transaction;
    }
}
