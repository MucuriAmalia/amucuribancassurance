package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 5/23/2017.
 */
@Entity
@Table(name = "sys_brk_fund_params")
public class SelfFundParams {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sfp_id")
    private Long sfpId;

    @Column(name = "sfp_app_level",nullable = false)
    private String applicableLevel;

    @Column(name = "sfp_app_value",nullable = false)
    private BigDecimal applicableValue;

    @Column(name = "sfp_pay_when_fund_exhaust")
    private boolean payWhenFundExhausted;

    @Column(name = "sfp_pay_when_benefit_exhaust")
    private boolean payWhenBenefitExhausted;

    @Column(name = "sfp_carry_forward_balances")
    private boolean carryForwardBalances;

    @Column(name = "sfp_min_deposit",nullable = false)
    private BigDecimal minDeposit;

    @Column(name = "sfp_fund_reset_amt",nullable = false)
    private BigDecimal fundResetAmount;

    @Column(name = "sfp_fund_deposit_amt",nullable = false)
    private BigDecimal fundDepositAmount;

    @Column(name = "sfp_billing_freq",nullable = false)
    private String billingFrequency;

    @Column(name = "sfp_deduct_adm_from_fund",nullable = false)
    private boolean deductAdminFeeFromFund;

    @Column(name = "sfp_balance")
    private BigDecimal selfFundBalance;

    @ManyToOne
    @JoinColumn(name="sfp_policy_id",nullable=false)
    private PolicyTrans policyTrans;

    public Long getSfpId() {
        return sfpId;
    }

    public void setSfpId(Long sfpId) {
        this.sfpId = sfpId;
    }

    public String getApplicableLevel() {
        return applicableLevel;
    }

    public void setApplicableLevel(String applicableLevel) {
        this.applicableLevel = applicableLevel;
    }

    public BigDecimal getApplicableValue() {
        return applicableValue;
    }

    public void setApplicableValue(BigDecimal applicableValue) {
        this.applicableValue = applicableValue;
    }

    public boolean isPayWhenFundExhausted() {
        return payWhenFundExhausted;
    }

    public void setPayWhenFundExhausted(boolean payWhenFundExhausted) {
        this.payWhenFundExhausted = payWhenFundExhausted;
    }

    public boolean isPayWhenBenefitExhausted() {
        return payWhenBenefitExhausted;
    }

    public void setPayWhenBenefitExhausted(boolean payWhenBenefitExhausted) {
        this.payWhenBenefitExhausted = payWhenBenefitExhausted;
    }

    public boolean isCarryForwardBalances() {
        return carryForwardBalances;
    }

    public void setCarryForwardBalances(boolean carryForwardBalances) {
        this.carryForwardBalances = carryForwardBalances;
    }

    public BigDecimal getMinDeposit() {
        return minDeposit;
    }

    public void setMinDeposit(BigDecimal minDeposit) {
        this.minDeposit = minDeposit;
    }

    public BigDecimal getFundResetAmount() {
        return fundResetAmount;
    }

    public void setFundResetAmount(BigDecimal fundResetAmount) {
        this.fundResetAmount = fundResetAmount;
    }

    public BigDecimal getFundDepositAmount() {
        return fundDepositAmount;
    }

    public void setFundDepositAmount(BigDecimal fundDepositAmount) {
        this.fundDepositAmount = fundDepositAmount;
    }

    public String getBillingFrequency() {
        return billingFrequency;
    }

    public void setBillingFrequency(String billingFrequency) {
        this.billingFrequency = billingFrequency;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public boolean isDeductAdminFeeFromFund() {
        return deductAdminFeeFromFund;
    }

    public void setDeductAdminFeeFromFund(boolean deductAdminFeeFromFund) {
        this.deductAdminFeeFromFund = deductAdminFeeFromFund;
    }

    public BigDecimal getSelfFundBalance() {
        return selfFundBalance;
    }

    public void setSelfFundBalance(BigDecimal selfFundBalance) {
        this.selfFundBalance = selfFundBalance;
    }
}
