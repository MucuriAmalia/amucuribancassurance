package com.brokersystems.brokerapp.certs.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 2/7/2017.
 */
public class EditRiskCertForm {

    private Long pcId;

    private String status;

    private String reasonCancelled;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateCancelled;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    public Long getPcId() {
        return pcId;
    }

    public void setPcId(Long pcId) {
        this.pcId = pcId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReasonCancelled() {
        return reasonCancelled;
    }

    public void setReasonCancelled(String reasonCancelled) {
        this.reasonCancelled = reasonCancelled;
    }

    public Date getDateCancelled() {
        return dateCancelled;
    }

    public void setDateCancelled(Date dateCancelled) {
        this.dateCancelled = dateCancelled;
    }

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
}
