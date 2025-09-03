package com.brokersystems.brokerapp.updatepolno.modal;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_brk_update_client_polno")
public class UpdateClientPolNo extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="update_id")
    private Long updateId;

    @Column(name = "insuremaster_pol_no")
    private String insuremasterPolNo;

    @Column(name = "risk_note_no")
    private String riskNote;

    @Column(name = "underwriter_pol_no")
    private String underwriterPolNo;

    @Column(name = "update_status")
    private String updateStatus;

    @Column(name = "uploaded_date")
    private Date uploadDate;

    @Column(name = "processed_date")
    private Date processedDate;

    @Column(name = "insurer_code")
    private String insurerCode;

    @Column(name = "underwriter_trans_code")
    private String underwriterTransCode;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pol_insurer")
    private AccountDef insurerId;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pol_client")
    private ClientDef clientDef;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private PolicyTrans updatedPolicy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "updated_product")
    private ProductsDef productId;

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public String getInsuremasterPolNo() {
        return insuremasterPolNo;
    }

    public void setInsuremasterPolNo(String insuremasterPolNo) {
        this.insuremasterPolNo = insuremasterPolNo;
    }

    public String getRiskNote() {
        return riskNote;
    }

    public void setRiskNote(String riskNote) {
        this.riskNote = riskNote;
    }

    public String getUnderwriterPolNo() {
        return underwriterPolNo;
    }

    public void setUnderwriterPolNo(String underwriterPolNo) {
        this.underwriterPolNo = underwriterPolNo;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public String getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(String insurerCode) {
        this.insurerCode = insurerCode;
    }

    public String getUnderwriterTransCode() {
        return underwriterTransCode;
    }

    public void setUnderwriterTransCode(String underwriterTransCode) {
        this.underwriterTransCode = underwriterTransCode;
    }

    public AccountDef getInsurerId() {
        return insurerId;
    }

    public void setInsurerId(AccountDef insurerId) {
        this.insurerId = insurerId;
    }

    public ClientDef getClientDef() {
        return clientDef;
    }

    public void setClientDef(ClientDef clientDef) {
        this.clientDef = clientDef;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public PolicyTrans getUpdatedPolicy() {
        return updatedPolicy;
    }

    public void setUpdatedPolicy(PolicyTrans updatedPolicy) {
        this.updatedPolicy = updatedPolicy;
    }

    public ProductsDef getProductId() {
        return productId;
    }

    public void setProductId(ProductsDef productId) {
        this.productId = productId;
    }
}
