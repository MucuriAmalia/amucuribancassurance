package com.brokersystems.brokerapp.certs.dto;

import java.util.Date;

public class PolicyCertificateDTO {

    private Long cqId;
    private Long certNo;
    private Date certWef;
    private Date policyWef;
    private Date policyWet;
    private String status;
    private String riskId;
    private String username;


    public Long getCqId() {
        return cqId;
    }

    public void setCqId(Long cqId) {
        this.cqId = cqId;
    }

    public Long getCertNo() {
        return certNo;
    }

    public void setCertNo(Long certNo) {
        this.certNo = certNo;
    }

    public Date getCertWef() {
        return certWef;
    }

    public void setCertWef(Date certWef) {
        this.certWef = certWef;
    }

    public Date getPolicyWef() {
        return policyWef;
    }

    public void setPolicyWef(Date policyWef) {
        this.policyWef = policyWef;
    }

    public Date getPolicyWet() {
        return policyWet;
    }

    public void setPolicyWet(Date policyWet) {
        this.policyWet = policyWet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
