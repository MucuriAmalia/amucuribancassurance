package com.brokersystems.brokerapp.certs.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by peter on 2/7/2017.
 */
public class RiskCertForm {

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    private Long riskId;

    private Long brnCertId;

    private Long subclasscertId;

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Long getBrnCertId() {
        return brnCertId;
    }

    public void setBrnCertId(Long brnCertId) {
        this.brnCertId = brnCertId;
    }

    public Long getSubclasscertId() {
        return subclasscertId;
    }

    public void setSubclasscertId(Long subclasscertId) {
        this.subclasscertId = subclasscertId;
    }
}
