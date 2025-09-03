package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "sys_brk_create_bulk_policy_risks")
public class BulkPolicyRisk extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bulk_policy_risk_Id")
    private Long risksId;

    @Column(name = "bulk_policy_risk_serial_no", nullable = false)
    private String serialNo;

    @Column(name = "bulk_policy_risk_sections", columnDefinition = "TEXT")
    private String sections;

    @Column(name = "bulk_policy_risk_limit")
    private BigDecimal limit;

    @Column(name = "bulk_policy_risk_rate")
    private Double rate;

    @Column(name = "bulk_policy_risk_div_factor")
    private Double divFactor;

    @ManyToOne
    @JoinColumn(name = "bulk_policy_id", nullable = false)
    private BulkPolicyCreation bulkPolicy;

    @Column(name = "bulk_policy_risk_compute")
    private boolean riskCompute;

    @Column(name = "bulk_policy_risk_status")
    private String riskStatus;

    @Column(name = "bulk_policy_risk_code")
    private String riskCode;

    public String getRiskCode() {
        return riskCode;
    }

    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public boolean isRiskCompute() {
        return riskCompute;
    }

    public void setRiskCompute(boolean riskCompute) {
        this.riskCompute = riskCompute;
    }

    public Long getRisksId() {
        return risksId;
    }

    public void setRisksId(Long risksId) {
        this.risksId = risksId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getDivFactor() {
        return divFactor;
    }

    public void setDivFactor(Double divFactor) {
        this.divFactor = divFactor;
    }

    public BulkPolicyCreation getBulkPolicy() {
        return bulkPolicy;
    }

    public void setBulkPolicy(BulkPolicyCreation bulkPolicy) {
        this.bulkPolicy = bulkPolicy;
    }

    @Override
    public String toString() {
        return "BulkPolicyRisk{" +
                "risksId=" + risksId +
                ", serialNo='" + serialNo + '\'' +
                ", sections='" + sections + '\'' +
                ", limit=" + limit +
                ", rate=" + rate +
                ", divFactor=" + divFactor +
                ", bulkPolicy=" + bulkPolicy +
                ", riskCompute=" + riskCompute +
                '}';
    }

}
