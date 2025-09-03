package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 5/16/2017.
 */
@Entity
@Table(name = "sys_brk_med_clm_requests")
public class MedicalParRequest {

    @Id
    @SequenceGenerator(name = "medParReqSeq",sequenceName = "med_par_req_seq",allocationSize=1)
    @GeneratedValue(generator = "medParReqSeq")
    @Column(name = "req_id")
    private Long reqId;

    @Column(name="req_date",nullable = false)
    private Date requestDate;

    @Column(name="req_type",nullable = false)
    private String requestType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="req_ail_id",nullable = false)
    private Ailments ailments;

    @Column(name = "req_serv_place")
    private String servicePlace;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="req_par_id",nullable = false)
    private MedicalParTrans parTrans;

    @Column(name="req_invoice_no")
    private String invoiceNo;

    @Column(name="req_invoice_amount")
    private BigDecimal invoiceAmount;

    @Column(name="req_calc_amount")
    private BigDecimal calcAmount;

    @Column(name="req_appr_amount")
    private BigDecimal approvedAmount;

    @Column(name="req_req_amount")
    private BigDecimal requestAmount;

    @Column(name="req_rej_amount")
    private BigDecimal rejectAmount;

    @Column(name="req_invoice_date")
    private Date invoiceDate;

    @Column(name="req_description",length = 2000)
    private String description;

    @Column(name="req_exgratia_amt")
    private BigDecimal exgratiaAmount;

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Ailments getAilments() {
        return ailments;
    }

    public void setAilments(Ailments ailments) {
        this.ailments = ailments;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MedicalParTrans getParTrans() {
        return parTrans;
    }

    public void setParTrans(MedicalParTrans parTrans) {
        this.parTrans = parTrans;
    }

    public BigDecimal getCalcAmount() {
        return calcAmount;
    }

    public void setCalcAmount(BigDecimal calcAmount) {
        this.calcAmount = calcAmount;
    }

    public BigDecimal getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(BigDecimal requestAmount) {
        this.requestAmount = requestAmount;
    }

    public String getServicePlace() {
        return servicePlace;
    }

    public void setServicePlace(String servicePlace) {
        this.servicePlace = servicePlace;
    }

    public BigDecimal getRejectAmount() {
        return rejectAmount;
    }

    public void setRejectAmount(BigDecimal rejectAmount) {
        this.rejectAmount = rejectAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getExgratiaAmount() {
        return exgratiaAmount;
    }

    public void setExgratiaAmount(BigDecimal exgratiaAmount) {
        this.exgratiaAmount = exgratiaAmount;
    }
}