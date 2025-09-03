package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sys_brk_wba_beneficiaries")
public class WIBABeneficiaries {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bwb_id")
    private Long bwbId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bwb_risk_id",nullable=false)
    private RiskTrans riskTrans;

    @Column(name = "bwb_emp_name",nullable = false,length = 80)
    private String fullName;

    @Column(name = "bwb_occupation",length = 40)
    private String occupation;

    @Column(name = "bwb_salary")
    private BigDecimal salary;

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Long getBwbId() {
        return bwbId;
    }

    public void setBwbId(Long bwbId) {
        this.bwbId = bwbId;
    }

    public RiskTrans getRiskTrans() {
        return riskTrans;
    }

    public void setRiskTrans(RiskTrans riskTrans) {
        this.riskTrans = riskTrans;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
