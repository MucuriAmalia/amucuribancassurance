package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_subagent_comm_rates")
public class SubAgentCommissionRates extends AuditBaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sub_comm_id")
    private Long commId;

    @Column(name="sub_comm_rate",nullable=false)
    private BigDecimal commRate;

    @Column(name="sub_comm_active")
    private boolean active;

    @Column(name="sub_comm_rate_desc")
    private String rateDesc;

    @Column(name="sub_comm_div_fact",nullable=false)
    private BigDecimal commDivFactor;

    @Column(name="sub_comm_rate_type")
    private String rateType;

    @Column(name="sub_comm_range_from",nullable=false)
    private BigDecimal commRangeFrom;

    @Column(name="sub_comm_range_to",nullable=false)
    private BigDecimal commRangeTo;

    @ManyToOne
    @JoinColumn(name="sub_comm_bdef_code",nullable=false)
    private BindersDef bindersDef;

    @ManyToOne
    @JoinColumn(name="sub_comm_rev_acct")
    private AccountTypes accountTypes;

    @ManyToOne
    @JoinColumn(name="sub_comm_rev_code")
    private RevenueItemsDef revenueItems;
    @Column(name="comm_applicable_at",length = 5)
    private String applicableAt;


    public String getApplicableAt() {
        return applicableAt;
    }

    public void setApplicableAt(String applicableAt) {
        this.applicableAt = applicableAt;
    }


    public Long getCommId() {
        return commId;
    }

    public void setCommId(Long commId) {
        this.commId = commId;
    }

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRateDesc() {
        return rateDesc;
    }

    public void setRateDesc(String rateDesc) {
        this.rateDesc = rateDesc;
    }

    public BigDecimal getCommDivFactor() {
        return commDivFactor;
    }

    public void setCommDivFactor(BigDecimal commDivFactor) {
        this.commDivFactor = commDivFactor;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getCommRangeFrom() {
        return commRangeFrom;
    }

    public void setCommRangeFrom(BigDecimal commRangeFrom) {
        this.commRangeFrom = commRangeFrom;
    }

    public BigDecimal getCommRangeTo() {
        return commRangeTo;
    }

    public void setCommRangeTo(BigDecimal commRangeTo) {
        this.commRangeTo = commRangeTo;
    }

    public BindersDef getBindersDef() {
        return bindersDef;
    }

    public void setBindersDef(BindersDef bindersDef) {
        this.bindersDef = bindersDef;
    }

    public AccountTypes getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(AccountTypes accountTypes) {
        this.accountTypes = accountTypes;
    }

    public RevenueItemsDef getRevenueItems() {
        return revenueItems;
    }

    public void setRevenueItems(RevenueItemsDef revenueItems) {
        this.revenueItems = revenueItems;
    }

}
