package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.accounts.model.PayeeAccounts;
import com.brokersystems.brokerapp.accounts.model.Payees;
import com.brokersystems.brokerapp.setup.model.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="sys_chq_trans")
public class ChequeTrans {

    @Id
    @SequenceGenerator(name = "chequeTransSeq",sequenceName = "cheque_trans_seq",allocationSize=1)
    @GeneratedValue(generator = "chequeTransSeq")
    @Column(name="ct_no")
    private Long ctNo;

    @Column(name = "ct_trans_source", length = 10)
    private String source;

    @Column(name = "ct_trans_src_type", length = 10)
    private String sourceType;

    @Column(name = "ct_trans_ref", length = 50)
    private String transRef;

    @Column(name = "ct_trans_ref_date",nullable =false)
    private Date transRefDate;

    @Column(name = "ct_narrative", length = 150)
    private String narrative;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_branch", nullable = false)
    private OrgBranch branch;

    @Column(name = "ct_remarks", length = 350)
    private String remarks;

    @Column(name = "ct_trans_amount", nullable = false)
    private BigDecimal transAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_payee_id",nullable=false)
    private Payees payee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_cur_code",nullable=false)
    private Currencies currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_ba_acc", nullable = false)
    private BankAccounts bankAccounts;

    private String chequeNumber;

    @Column(name = "ct_chq_date")
    private Date chequeDate;

    @Column(name = "ct_chq_words", length = 1000)
    private String chequeWords;

    @Column(name = "ct_chq_status", length = 20)
    private String chqStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_source_posted_by")
    private User postedUser;

    @Column(name = "ct_source_posted_date")
    private Date postedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_raised_by")
    private User raisedBy;

    @Column(name = "ct_raised_date")
    private Date raisedDate;

    @Column(name = "ct_payment_type", length = 20)
    private String paymentType;

    @Column(name = "ct_invoice_no", length = 20)
    private String invoiceNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_paymode_id")
    private PaymentModes paymentModes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ct_payee_act_id")
    private PayeeAccounts payeeAccounts;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public PayeeAccounts getPayeeAccounts() {
        return payeeAccounts;
    }

    public void setPayeeAccounts(PayeeAccounts payeeAccounts) {
        this.payeeAccounts = payeeAccounts;
    }

    public PaymentModes getPaymentModes() {
        return paymentModes;
    }

    public void setPaymentModes(PaymentModes paymentModes) {
        this.paymentModes = paymentModes;
    }

    public Long getCtNo() {
        return ctNo;
    }

    public void setCtNo(Long ctNo) {
        this.ctNo = ctNo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTransRef() {
        return transRef;
    }

    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }

    public Date getTransRefDate() {
        return transRefDate;
    }

    public void setTransRefDate(Date transRefDate) {
        this.transRefDate = transRefDate;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }


    public Payees getPayee() {
        return payee;
    }

    public void setPayee(Payees payee) {
        this.payee = payee;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public void setCurrency(Currencies currency) {
        this.currency = currency;
    }

    public BankAccounts getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(BankAccounts bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public Date getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(Date chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getChequeWords() {
        return chequeWords;
    }

    public void setChequeWords(String chequeWords) {
        this.chequeWords = chequeWords;
    }

    public String getChqStatus() {
        return chqStatus;
    }

    public void setChqStatus(String chqStatus) {
        this.chqStatus = chqStatus;
    }

    public User getPostedUser() {
        return postedUser;
    }

    public void setPostedUser(User postedUser) {
        this.postedUser = postedUser;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public User getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }

    public Date getRaisedDate() {
        return raisedDate;
    }

    public void setRaisedDate(Date raisedDate) {
        this.raisedDate = raisedDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}
