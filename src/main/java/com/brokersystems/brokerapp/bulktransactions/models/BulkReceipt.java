package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.model.SystemTransactionsTemp;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sys_brk_bulk_receipt")
public class BulkReceipt extends AuditBaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "receiptTransSeq",sequenceName = "receipt_trans_seq",allocationSize=1)
    @GeneratedValue(generator = "receiptTransSeq")
    @Column(name="bulk_receipt_id")
    private Long receiptId;

    @Column(name="bulk_trans_id")
    private Long transId;

    @Column(name="bulk_receipt_upload_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date uploadDate;

    @Column(name="bulk_receipt_amount")
    private BigDecimal receiptAmount;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bulk_receipt_acct_id", nullable = true)
    private AccountDef insurance;

    @Column(name="bulk_receipt_paid_by")
    private String paidBy;

    @Column(name="bulk_receipt_pol_no")
    private String polNo;

    @Column(name="bulk_receipt_payment_ref")
    private String paymentRef;

    @Column(name="bulk_receipt_manual_ref")
    private String manualRef;

    @Column(name="bulk_receipt_doc_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date documentDate;

    @Column(name="bulk_receipt_desc")
    private String receiptDesc;

    @Column(name="bulk_receipt_status")
    private String receiptStatus;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bulk_receipt_brn_code")
    private OrgBranch branch;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bulk_receipt_ten_id")
    private ClientDef client;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bulk_receipt_uploaded_by")
    private User receiptUploadedBy;

    @Column(name = "bulk_receipt_type")
    private String receiptType;

    @Column(name = "bulk_receipt_payment_mode")
    private String paymentMode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "bulk_receipt_policy")
    private PolicyTrans policy;

    public String getPaymentMode () {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPolNo() {
        return polNo;
    }

    public void setPolNo(String polNo) {
        this.polNo = polNo;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public AccountDef getInsurance() {
        return insurance;
    }

    public void setInsurance(AccountDef insurance) {
        this.insurance = insurance;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getManualRef() {
        return manualRef;
    }

    public void setManualRef(String manualRef) {
        this.manualRef = manualRef;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getReceiptDesc() {
        return receiptDesc;
    }

    public void setReceiptDesc(String receiptDesc) {
        this.receiptDesc = receiptDesc;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public ClientDef getClient() {
        return client;
    }

    public void setClient(ClientDef client) {
        this.client = client;
    }

    public User getReceiptUploadedBy() {
        return receiptUploadedBy;
    }

    public void setReceiptUploadedBy(User receiptUploadedBy) {
        this.receiptUploadedBy = receiptUploadedBy;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public PolicyTrans getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTrans policy) {
        this.policy = policy;
    }

    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }

}
