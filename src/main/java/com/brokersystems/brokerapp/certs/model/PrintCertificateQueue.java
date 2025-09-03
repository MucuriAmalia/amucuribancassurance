package com.brokersystems.brokerapp.certs.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.RiskTrans;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by usert1 on 2/1/2017.
 */
@Entity
@Table(name="sys_brk_cert_queue")
public class PrintCertificateQueue {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="cq_id")
    private Long cqId;

    @ManyToOne
    @JoinColumn(name="cq_rsk_id",nullable=false)
    private RiskTrans risk;

    @ManyToOne
    @JoinColumn(name="cq_bc_id",nullable=true)
    private BranchCerts cert;

    @ManyToOne
    @JoinColumn(name="cq_sbccert_id",nullable=true)
    private SubclassCertTypes subclassCertTypes;

    @OneToOne
    @JoinColumn(name="cq_pc_id",nullable=false)
    private PolicyCerts policyCerts;

    @Column(name="cq_date_time",nullable = false)
    private Date certWef;

    @Column(name="cq_status",nullable = false)
    private String status;

    @Column(name="cq_cert_no")
    private Long certNo;

    @Column(name="cq_gd_print")
    private String goodForPrint;

    @Column(name="cq_cert_yr")
    private Long certYear;

    @Column(name="cq_client_name")
    private String clientName;

    @ManyToOne
    @JoinColumn(name="cq_done_by",nullable=false)
    private User doneBy;

    @ManyToOne
    @JoinColumn(name="cq_alloc_by")
    private User allocBy;

    @ManyToOne
    @JoinColumn(name="cq_print_by")
    private User printBy;
    @Column(name="cq_aki_cert_no",length = 30)
    private String akiCertNo;
    @Column(name="cq_aki_req_no",length = 30)
    private String akiRequestNo;
    @Column(name="cq_aki_req_key",length = 30)
    private String akiRequestKey;

    @Column(name="cq_aki_error_message",length = 2000)
    private String errorMessage;

    @Column(name="cq_aki_issued",length = 3)
    private String issued;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getAkiCertNo() {
        return akiCertNo;
    }

    public void setAkiCertNo(String akiCertNo) {
        this.akiCertNo = akiCertNo;
    }

    public String getAkiRequestNo() {
        return akiRequestNo;
    }

    public void setAkiRequestNo(String akiRequestNo) {
        this.akiRequestNo = akiRequestNo;
    }

    public String getAkiRequestKey() {
        return akiRequestKey;
    }

    public void setAkiRequestKey(String akiRequestKey) {
        this.akiRequestKey = akiRequestKey;
    }

    public Long getCqId() {
        return cqId;
    }

    public void setCqId(Long cqId) {
        this.cqId = cqId;
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

    public PolicyCerts getPolicyCerts() {
        return policyCerts;
    }

    public void setPolicyCerts(PolicyCerts policyCerts) {
        this.policyCerts = policyCerts;
    }

    public String getGoodForPrint() {
        return goodForPrint;
    }

    public void setGoodForPrint(String goodForPrint) {
        this.goodForPrint = goodForPrint;
    }

    public Long getCertYear() {
        return certYear;
    }

    public void setCertYear(Long certYear) {
        this.certYear = certYear;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public User getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(User doneBy) {
        this.doneBy = doneBy;
    }

    public User getAllocBy() {
        return allocBy;
    }

    public void setAllocBy(User allocBy) {
        this.allocBy = allocBy;
    }

    public User getPrintBy() {
        return printBy;
    }

    public void setPrintBy(User printBy) {
        this.printBy = printBy;
    }

    public SubclassCertTypes getSubclassCertTypes() {
        return subclassCertTypes;
    }

    public void setSubclassCertTypes(SubclassCertTypes subclassCertTypes) {
        this.subclassCertTypes = subclassCertTypes;
    }
}
