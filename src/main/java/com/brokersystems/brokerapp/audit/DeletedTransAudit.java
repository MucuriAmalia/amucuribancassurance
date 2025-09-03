package com.brokersystems.brokerapp.audit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_deleted_trans_audit")
public class DeletedTransAudit {

    public DeletedTransAudit() {
    }

    public DeletedTransAudit(String transType, String doneby, String transRefNo, BigDecimal premium) {
        this.transType = transType;
        this.doneby = doneby;
        this.transRefNo = transRefNo;
        this.premium = premium;
        this.deletedAt = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dta_id")
    private Long dtaId;

    @Column(name = "dta_trans_type",nullable = false)
    private String transType;

    @Column(name = "dta_done_by",nullable =false)
    private String doneby;

    @Column(name = "dta_deleted_at")
    private Date deletedAt;

    @Column(name = "dta_trans_ref_no",nullable = false)
    private String transRefNo;

    @Column(name = "dta_trans_prem")
    private BigDecimal premium;


    public Long getDtaId() {
        return dtaId;
    }

    public void setDtaId(Long dtaId) {
        this.dtaId = dtaId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getDoneby() {
        return doneby;
    }

    public void setDoneby(String doneby) {
        this.doneby = doneby;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTransRefNo() {
        return transRefNo;
    }

    public void setTransRefNo(String transRefNo) {
        this.transRefNo = transRefNo;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }
}
