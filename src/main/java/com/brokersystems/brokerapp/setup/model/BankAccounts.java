package com.brokersystems.brokerapp.setup.model;


import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="sys_brk_bnk_accounts")
public class BankAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ba_id")
    private Long baId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ba_sys_br_code", nullable = false)
    private OrgBranch branch;

    @Column(name = "ba_acct_no", length = 50)
    private String bankAcctNumber;

    @Column(name = "ba_acct_name", length = 150)
    private String bankAcctName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ba_branch_code")
    private BankBranches bankBranches;

    @Column(name = "ba_check_no", length = 150)
    private String currentCheckNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ba_gl_acc", nullable = false)
    private CoaSubAccounts glAcc;

    @Column(name = "ba_status",nullable =false)
    private String status;

    @Column(name = "ba_max_amount")
    private BigDecimal maximumAmount;

    @Column(name = "ba_min_amount")
    private BigDecimal minAmount;

    @Column(name = "ba_type",length = 1)
    private String transtype;

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public Long getBaId() {
        return baId;
    }

    public void setBaId(Long baId) {
        this.baId = baId;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public String getBankAcctNumber() {
        return bankAcctNumber;
    }

    public void setBankAcctNumber(String bankAcctNumber) {
        this.bankAcctNumber = bankAcctNumber;
    }

    public String getBankAcctName() {
        return bankAcctName;
    }

    public void setBankAcctName(String bankAcctName) {
        this.bankAcctName = bankAcctName;
    }

    public BankBranches getBankBranches() {
        return bankBranches;
    }

    public void setBankBranches(BankBranches bankBranches) {
        this.bankBranches = bankBranches;
    }

    public String getCurrentCheckNo() {
        return currentCheckNo;
    }

    public void setCurrentCheckNo(String currentCheckNo) {
        this.currentCheckNo = currentCheckNo;
    }

    public CoaSubAccounts getGlAcc() {
        return glAcc;
    }

    public void setGlAcc(CoaSubAccounts glAcc) {
        this.glAcc = glAcc;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }
}
