package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_sub_account_types")
public class SubAccountTypes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sat_acc_id")
    private Long subAcctId;

    @ManyToOne
    @JoinColumn(name="sat_acc_type",nullable=false)
    private AccountTypes accountTypes;

    @Column(name="sat_sht_desc",nullable=false,unique=true)
    private String accShtDesc;

    @Column(name="sat_name")
    private String accName;

    private BigDecimal commRate;

    @ManyToOne
    @JoinColumn(name="sat_binder_def",nullable=true)
    private BindersDef bindersDef;

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public Long getSubAcctId() {
        return subAcctId;
    }

    public void setSubAcctId(Long subAcctId) {
        this.subAcctId = subAcctId;
    }

    public AccountTypes getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(AccountTypes accountTypes) {
        this.accountTypes = accountTypes;
    }

    public String getAccShtDesc() {
        return accShtDesc;
    }

    public void setAccShtDesc(String accShtDesc) {
        this.accShtDesc = accShtDesc;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public BindersDef getBindersDef() {
        return bindersDef;
    }

    public void setBindersDef(BindersDef bindersDef) {
        this.bindersDef = bindersDef;
    }
}
