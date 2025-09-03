package com.brokersystems.brokerapp.uw.model;


import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sys_brk_wba_dtls")
public class WIBADetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bwd_id")
    private Long bwdId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bwd_risk_id",nullable=false)
    private RiskTrans riskTrans;

    @Column(name = "bwd_tot_salary")
    private BigDecimal estimatedTotSalary;

    @Column(name = "bwd_funeral_cover")
    private BigDecimal estFuneralCover;

    @Column(name = "bwd_med_employees")
    private BigDecimal estMedicalCover;

    @Column(name = "bwd_no_employees")
    private Long totEmployees;


    public Long getBwdId() {
        return bwdId;
    }

    public void setBwdId(Long bwdId) {
        this.bwdId = bwdId;
    }

    public RiskTrans getRiskTrans() {
        return riskTrans;
    }

    public void setRiskTrans(RiskTrans riskTrans) {
        this.riskTrans = riskTrans;
    }

    public BigDecimal getEstimatedTotSalary() {
        return estimatedTotSalary;
    }

    public void setEstimatedTotSalary(BigDecimal estimatedTotSalary) {
        this.estimatedTotSalary = estimatedTotSalary;
    }

    public BigDecimal getEstFuneralCover() {
        return estFuneralCover;
    }

    public void setEstFuneralCover(BigDecimal estFuneralCover) {
        this.estFuneralCover = estFuneralCover;
    }

    public BigDecimal getEstMedicalCover() {
        return estMedicalCover;
    }

    public void setEstMedicalCover(BigDecimal estMedicalCover) {
        this.estMedicalCover = estMedicalCover;
    }

    public Long getTotEmployees() {
        return totEmployees;
    }

    public void setTotEmployees(Long totEmployees) {
        this.totEmployees = totEmployees;
    }
}
