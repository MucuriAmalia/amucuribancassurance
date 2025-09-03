package com.brokersystems.brokerapp.certs.model;

import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by usert1 on 2/1/2017.
 */
@Entity
@Table(name="sys_brk_policy_certs")
public class PolicyCerts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pc_id")
    private Long pcId;

    @ManyToOne
    @JoinColumn(name="pc_rsk_id",nullable=false)
    private RiskTrans risk;

    @ManyToOne
    @JoinColumn(name="pc_bc_id",nullable=true)
    private BranchCerts cert;

    @Column(name="pc_wef",nullable = false)
    private Date certWef;

    @Column(name="pc_wet",nullable = false)
    private Date certWet;

    @Column(name="pc_print_dt")
    private Date printDt;

    @Column(name="pc_status",nullable = false)
    private String status;

    @Column(name="pc_print_status",nullable = false)
    private String printStatus;

    @Column(name="pc_cert_no")
    private Long certNo;

    @Column(name="pc_reason_cancelled",length = 1000)
    private String reasonCancelled;

    @Column(name="pc_cancel_dt")
    private Date cancelDate;

    @Column(name="pc_risk_code",nullable = false)
    private Long riskId;

    @ManyToOne
    @JoinColumn(name="pc_alloc_by")
    private User allocBy;

    @ManyToOne
    @JoinColumn(name="pc_sbccert_id",nullable=true)
    private SubclassCertTypes subclassCertTypes;


    public Long getPcId() {
        return pcId;
    }

    public void setPcId(Long pcId) {
        this.pcId = pcId;
    }

    public RiskTrans getRisk() {
        return risk;
    }

    public void setRisk(RiskTrans risk) {
        this.risk = risk;
    }

    public BranchCerts getCert() {
        return cert;
    }

    public void setCert(BranchCerts cert) {
        this.cert = cert;
    }

    public Date getCertWef() {
        return certWef;
    }

    public void setCertWef(Date certWef) {
        this.certWef = certWef;
    }

    public Date getCertWet() {
        return certWet;
    }

    public void setCertWet(Date certWet) {
        this.certWet = certWet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCertNo() {
        return certNo;
    }

    public void setCertNo(Long certNo) {
        this.certNo = certNo;
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Date getPrintDt() {
        return printDt;
    }

    public void setPrintDt(Date printDt) {
        this.printDt = printDt;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public String getReasonCancelled() {
        return reasonCancelled;
    }

    public void setReasonCancelled(String reasonCancelled) {
        this.reasonCancelled = reasonCancelled;
    }

    public User getAllocBy() {
        return allocBy;
    }

    public void setAllocBy(User allocBy) {
        this.allocBy = allocBy;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public SubclassCertTypes getSubclassCertTypes() {
        return subclassCertTypes;
    }

    public void setSubclassCertTypes(SubclassCertTypes subclassCertTypes) {
        this.subclassCertTypes = subclassCertTypes;
    }
}
