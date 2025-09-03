
package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.medical.model.BinderMedicalCards;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
@Entity
//@Cacheable(true)
@Table(name="sys_brk_quotations")
public class QuoteTrans {

    @Id
    @GeneratedValue(generator = "myPolicySeq")
    @SequenceGenerator(name = "myPolicySeq",sequenceName = "policy_seq",allocationSize=1)
    @Column(name="quot_id")
    private Long quoteId;

    @Column(name="quot_no",nullable=false)
    private String quotNo;

    @Column(name="quot_rev_no",nullable=false)
    private String quotRevNo;

    @Column(name="quot_date",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date quotDate;

    @Column(name="quot_status",nullable=false)
    private String quotStatus;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_prep_user")
    private User preparedBy;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_auth_user")
    private User authorisedBy;

    @Column(name="quot_auth_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date quotAuthDate;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_curr_id")
    private Currencies transCurrency;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_client_id")
    private ClientDef client;

    @Column(name = "quot_vat_amt")
    private BigDecimal vatAmount;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_prs_id")
    private ProspectDef prospect;

    @Column(name="quot_clnt_type")
    private String clientType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_prod_id")
    private ProductsDef product;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_binder_id")
    private BindersDef binder;

    @Column(name="quot_business_type")
    private String businessType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_branch_id")
    private OrgBranch branch;

    @Column(name="quot_wef_date", nullable=false)
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="quot_wet_date",nullable=false)
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @Column(name="quot_expiry_date",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_source_id")
    private BusinessSources source;

    @Column(name="quot_internal_comm",length = 2000)
    private String internalComments;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_pmode_id")
    private PaymentModes paymentMode;

    @Column(name="quot_sum_insured")
    private BigDecimal sumInsured;

    @Column(name="quot_premium")
    private BigDecimal premium;

    @Column(name="quot_basic_premium")
    private BigDecimal basicPrem;

    @Column(name="quot_net_premium")
    private BigDecimal netPrem;

    @Column(name="quot_tl")
    private BigDecimal trainingLevy;

    @Column(name="quot_phcf")
    private BigDecimal phcf;

    @Column(name="quot_sd")
    private BigDecimal stampDuty;

    @Column(name="quot_whtx")
    private BigDecimal whtx;

    @Column(name="quot_extras")
    private BigDecimal extras;

    @Column(name="quot_comm_amt")
    private BigDecimal commAmt;

    @Column(name="quot_medical_type")
    private String medicalCoverType;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_crd_id")
    private BinderMedicalCards binCardType;

    @OneToOne
    @JoinColumn(name = "quot_policy")
    private PolicyTrans convertedReference;

    @Column(name = "quot_service_charge")
    private BigDecimal serviceCharge;

    @Column(name = "quot_issue_card_fee")
    private BigDecimal issueCardFee;



    @JsonIgnore
    @OneToMany(mappedBy="quoteTrans")
    private List<QuoteProTrans> quoteProTranses;

    @Column(name="quot_cancel_reason",length = 2000)
    private String cancelReasons;

    @Column(name="quot_type")
    private String quoteType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_sub_agent_id")
    private AccountDef subAgent;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_introducer_agent_id")
    private AccountDef introducerAgent;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_marketer_agent_id")
    private AccountDef marketerAgent;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_leadsman_id")
    private User leadsMan;

    @Column(name = "quot_activication_code")
    private String activationCode;
    @Column(name="quot_introducer_absa_no")
    private String absaNoIntroducer;

    @Column(name="quot_leadsman_absa_no")
    private String absaNoLeadsMan;

    @Column(name="quot_subagent_absa_no")
    private String absaNoSubAgent;

    @Column(name="quot_marketer_absa_no")
    private String absaNoMarketer;

    public String getAbsaNoLeadsMan() {
        return absaNoLeadsMan;
    }

    public void setAbsaNoLeadsMan(String absaNoLeadsMan) {
        this.absaNoLeadsMan = absaNoLeadsMan;
    }

    public String getAbsaNoSubAgent() {
        return absaNoSubAgent;
    }

    public void setAbsaNoSubAgent(String absaNoSubAgent) {
        this.absaNoSubAgent = absaNoSubAgent;
    }

    public String getAbsaNoMarketer() {
        return absaNoMarketer;
    }

    public void setAbsaNoMarketer(String absaNoMarketer) {
        this.absaNoMarketer = absaNoMarketer;
    }

    public String getAbsaNoIntroducer() {
        return absaNoIntroducer;
    }

    public void setAbsaNoIntroducer(String absaNo) {
        this.absaNoIntroducer = absaNo;
    }

    public User getLeadsMan() {
        return leadsMan;
    }

    public void setLeadsMan(User leadsMan) {
        this.leadsMan = leadsMan;
    }

    public AccountDef getSubAgent() {
        return subAgent;
    }

    public void setSubAgent(AccountDef subAgent) {
        this.subAgent = subAgent;
    }

    public AccountDef getIntroducerAgent() {
        return introducerAgent;
    }

    public void setIntroducerAgent(AccountDef introducerAgent) {
        this.introducerAgent = introducerAgent;
    }

    public AccountDef getMarketerAgent() {
        return marketerAgent;
    }

    public void setMarketerAgent(AccountDef marketerAgent) {
        this.marketerAgent = marketerAgent;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuotNo() {
        return quotNo;
    }

    public void setQuotNo(String quotNo) {
        this.quotNo = quotNo;
    }

    public String getQuotRevNo() {
        return quotRevNo;
    }

    public void setQuotRevNo(String quotRevNo) {
        this.quotRevNo = quotRevNo;
    }

    public Date getQuotDate() {
        return quotDate;
    }

    public void setQuotDate(Date quotDate) {
        this.quotDate = quotDate;
    }

    public String getQuotStatus() {
        return quotStatus;
    }

    public void setQuotStatus(String quotStatus) {
        this.quotStatus = quotStatus;
    }

    public User getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(User preparedBy) {
        this.preparedBy = preparedBy;
    }

    public Currencies getTransCurrency() {
        return transCurrency;
    }

    public void setTransCurrency(Currencies transCurrency) {
        this.transCurrency = transCurrency;
    }

    public ClientDef getClient() {
        return client;
    }

    public void setClient(ClientDef client) {
        this.client = client;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BusinessSources getSource() {
        return source;
    }

    public void setSource(BusinessSources source) {
        this.source = source;
    }

    public String getInternalComments() {
        return internalComments;
    }

    public void setInternalComments(String internalComments) {
        this.internalComments = internalComments;
    }

    public PaymentModes getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentModes paymentMode) {
        this.paymentMode = paymentMode;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getBasicPrem() {
        return basicPrem;
    }

    public void setBasicPrem(BigDecimal basicPrem) {
        this.basicPrem = basicPrem;
    }

    public BigDecimal getNetPrem() {
        return netPrem;
    }

    public void setNetPrem(BigDecimal netPrem) {
        this.netPrem = netPrem;
    }

    public BigDecimal getTrainingLevy() {
        return trainingLevy;
    }

    public void setTrainingLevy(BigDecimal trainingLevy) {
        this.trainingLevy = trainingLevy;
    }

    public BigDecimal getPhcf() {
        return phcf;
    }

    public void setPhcf(BigDecimal phcf) {
        this.phcf = phcf;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getWhtx() {
        return whtx;
    }

    public void setWhtx(BigDecimal whtx) {
        this.whtx = whtx;
    }

    public BigDecimal getExtras() {
        return extras;
    }

    public void setExtras(BigDecimal extras) {
        this.extras = extras;
    }

    public List<QuoteProTrans> getQuoteProTranses() {
        return quoteProTranses;
    }

    public void setQuoteProTranses(List<QuoteProTrans> quoteProTranses) {
        this.quoteProTranses = quoteProTranses;
    }

    public BigDecimal getCommAmt() {
        return commAmt;
    }

    public void setCommAmt(BigDecimal commAmt) {
        this.commAmt = commAmt;
    }

    public User getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(User authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public Date getQuotAuthDate() {
        return quotAuthDate;
    }

    public void setQuotAuthDate(Date quotAuthDate) {
        this.quotAuthDate = quotAuthDate;
    }

    public ProspectDef getProspect() {
        return prospect;
    }

    public void setProspect(ProspectDef prospect) {
        this.prospect = prospect;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getCancelReasons() {
        return cancelReasons;
    }

    public void setCancelReasons(String cancelReasons) {
        this.cancelReasons = cancelReasons;
    }

    public ProductsDef getProduct() {
        return product;
    }

    public void setProduct(ProductsDef product) {
        this.product = product;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getMedicalCoverType() {
        return medicalCoverType;
    }

    public void setMedicalCoverType(String medicalCoverType) {
        this.medicalCoverType = medicalCoverType;
    }

//
//    public String getIssueCard() {
//        return issueCard;
//    }
//
//    public void setIssueCard(String issueCard) {
//        this.issueCard = issueCard;
//    }

//    public String getCardType() {
//        return cardType;
//    }
//
//    public void setCardType(String cardType) {
//        this.cardType = cardType;
//    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getIssueCardFee() {
        return issueCardFee;
    }

    public void setIssueCardFee(BigDecimal issueCardFee) {
        this.issueCardFee = issueCardFee;
    }

    public PolicyTrans getConvertedReference() {
        return convertedReference;
    }

    public void setConvertedReference(PolicyTrans convertedReference) {
        this.convertedReference = convertedReference;
    }

    public BinderMedicalCards getBinCardType() {
        return binCardType;
    }

    public void setBinCardType(BinderMedicalCards binCardType) {
        this.binCardType = binCardType;
    }


    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuoteTrans that = (QuoteTrans) o;

        return quoteId != null ? quoteId.equals(that.quoteId) : that.quoteId == null;

    }

    @Override
    public int hashCode() {
        return quoteId != null ? quoteId.hashCode() : 0;
    }
}