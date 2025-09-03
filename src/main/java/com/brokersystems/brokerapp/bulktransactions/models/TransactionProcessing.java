package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_trans_processing")
public class TransactionProcessing extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "trans_processing_id")
    private Long transProcessingId;

    @Column(name = "trans_processing_pol_no")
    private String polNumber;

    @Column(name = "trans_processing_serial_no")
    private String serialNo;

    @Column(name = "trans_processing_pro_name")
    private String productName;

    @Column(name = "trans_processing_pol_code")
    private String polCode;

    @Column(name = "trans_processing_wef")
    private Date coverDateFrom;

    @Column(name = "trans_processing_wet")
    private Date coverDateTo;

    @Column(name = "trans_processing_renew_date")
    private Date renewDate;

    @Column(name = "trans_processing_currency")
    private String currency;

    @Column(name = "trans_processing_inception_date")
    private Date inceptionDate;

    @Column(name = "trans_processing_proposer_code")
    private String proposerCode;

    @Column(name = "trans_processing_client_pin")
    private String clientPin;

    @Column(name = "trans_processing_underwriter")
    private String underwriter;

    @Column(name = "trans_processing_cover_type")
    private String coverType;

    @Column(name = "trans_processing_sum_insured")
    private BigDecimal sumInsured ;

    @Column(name = "trans_processing_gross_premium")
    private BigDecimal grossPremium;

    @Column(name = "trans_processing_processed", length = 10)
    private String transProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "trans_processing_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "trans_processing_processed_by")
    private User processedBy;

    @Column(name = "trans_processing_date_uploaded")
    private Date dateUploaded;

    @Column(name = "trans_processing_date_processed")
    private Date dateProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "trans_processing_pol_id")
    private PolicyTrans policyTrans;

    @Column(name = "trans_processing_bal")
    private BigDecimal balance;

    @Column(name = "trans_processing_clnt_fname")
    private String clientFname;

    @Column(name = "trans_processing_clnt_other_names")
    private String clientOtherNames;

    @Column(name = "trans_processing_clnt_id_no")
    private String clientIDNo;

    @Column(name = "trans_processing_client_email")
    private String clientEmail;

    @Column(name = "trans_processing_client_phone_no")
    private String clientPhoneNo;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trans_processing_loaded_by")
    private User loadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trans_processing_branch")
    private OrgBranch branch;

    @Column(name = "trans_processing_client_type")
    private String clientType;

    @Column(name = "trans_processing_client_dob")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date clientDOB;

    @Column(name = "trans_processing_client_cif")
    private String clientCIF;

    @Column(name = "trans_processing_bank_commm")
    private BigDecimal bankComm;

    @Column(name = "trans_processing_agent_comm")
    private BigDecimal agentComm;

    @Column(name = "trans_processing_subAgent_abno")
    private String subAgnetABNo;

    @Column(name = "trans_processing_marketer_abno")
    private String marketerABNo;

    @Column(name = "trans_processing_leads_abno")
    private String leadsABNo;

    @Column(name = "trans_processing_stamp_duty")
    private BigDecimal stampDuty;

    @Column(name = "trans_processing_marketer_whtx")
    private BigDecimal whtx;

    @Column(name = "trans_processing_extras")
    private BigDecimal extras;

    @Column(name = "trans_processing_training_levy")
    private BigDecimal trainingLevy;

    @Column(name = "trans_processing_phfc")
    private BigDecimal phFund;

    @Column(name = "trans_processing_basic_premium")
    private BigDecimal premium;

    @Column(name = "trans_processing_net_premium")
    private BigDecimal netPremium;

    @Column(name = "trans_processing_paid_premium")
    private BigDecimal paidPremium;

    @Column(name = "trans_processing_frequency")
    private String frequency;

    @Column(name = "trans_processing_paid_instals")
    private Long paidInsts;

    @Column(name="trans_processing_total_insts")
    private Integer totalInstalments;

    @Column(name = "trans_processing_ref_code")
    private String transRefCode;

    @Column(name = "trans_processing_type")
    private String transType;

    @Column(name = "trans_processing_authorised_by")
    private String authorisedBy;

    @Column(name = "trans_processing_authorised_date")
    private Date authorisedDate;

    @Column(name = "trans_processing_coinsurance_flag")
    private String coinsuranceFlag;

    @Column(name = "trans_processing_coinsurance_percentage")
    private BigDecimal coinsurancePercentage;

    @Column(name = "trans_processing_coinsurance_leader_flag")
    private String coinsuranceLeaderFlag;

    @Column(name = "trans_processing_section_code")
    private String sectionCode;

    @Column(name = "trans_processing_policy_insured_code")
    private String policyInsuredCode;

    @Column(name = "trans_processing_risk_code")
    private String riskCode;

    @Column(name = "trans_processing_is_renewable")
    private String isRenewable;

    @Column(name = "trans_processing_subclass_code")
    private String subclassCode;

    @Column(name = "trans_processing_risk_sum_assured")
    private BigDecimal riskSumAssured;

    @Column(name = "trans_processing_covertype_code")
    private String coverTypeCode;

    @Column(name = "trans_processing_risk_id")
    private String riskId;

    @Column(name = "trans_processing_agent_code")
    private String agentCode;

//    @Column(name = "trans_processing_policy_status")
//    private String policyStatus;

    public String getPolCode() {
        return polCode;
    }


    public String getProposerCode() {
        return proposerCode;
    }

    public void setProposerCode(String proposerCode) {
        this.proposerCode = proposerCode;
    }

    public void setPolCode(String polCode) {
        this.polCode = polCode;
    }

    public BigDecimal getRiskSumAssured() {
        return riskSumAssured;
    }

    public void setRiskSumAssured(BigDecimal riskSumAssured) {
        this.riskSumAssured = riskSumAssured;
    }

    public String getSubclassCode() {
        return subclassCode;
    }

    public void setSubclassCode(String subclassCode) {
        this.subclassCode = subclassCode;
    }

    public String getCoverTypeCode() {
        return coverTypeCode;
    }

    public void setCoverTypeCode(String coverTypeCode) {
        this.coverTypeCode = coverTypeCode;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

//    public String getPolicyStatus() {
//        return policyStatus;
//    }
//
//    public void setPolicyStatus(String policyStatus) {
//        this.policyStatus = policyStatus;
//    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    // Add these getter methods with the other getters
    public String getAuthorisedBy() {
        return authorisedBy;
    }

    public Date getAuthorisedDate() {
        return authorisedDate;
    }

    public String getCoinsuranceFlag() {
        return coinsuranceFlag;
    }

    public BigDecimal getCoinsurancePercentage() {
        return coinsurancePercentage;
    }

    public String getCoinsuranceLeaderFlag() {
        return coinsuranceLeaderFlag;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getPolicyInsuredCode() {
        return policyInsuredCode;
    }

    public String getRiskCode() {
        return riskCode;
    }

    public String getIsRenewable() {
        return isRenewable;
    }

    public void setAuthorisedBy(String authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public void setAuthorisedDate(Date authorisedDate) {
        this.authorisedDate = authorisedDate;
    }

    public void setCoinsuranceFlag(String coinsuranceFlag) {
        this.coinsuranceFlag = coinsuranceFlag;
    }

    public void setCoinsurancePercentage(BigDecimal coinsurancePercentage) {
        this.coinsurancePercentage = coinsurancePercentage;
    }

    public void setCoinsuranceLeaderFlag(String coinsuranceLeaderFlag) {
        this.coinsuranceLeaderFlag = coinsuranceLeaderFlag;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public void setPolicyInsuredCode(String policyInsuredCode) {
        this.policyInsuredCode = policyInsuredCode;
    }

    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    public void setIsRenewable(String isRenewable) {
        this.isRenewable = isRenewable;
    }

    public String getClientFname() {
        return clientFname;
    }

    public void setClientFname(String clientFname) {
        this.clientFname = clientFname;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransRefCode() {
        return transRefCode;
    }

    public void setTransRefCode(String transRefCode) {
        this.transRefCode = transRefCode;
    }

    public Long getTransProcessingId() {
        return transProcessingId;
    }

    public void setTransProcessingId(Long transProcessingId) {
        this.transProcessingId = transProcessingId;
    }

    public String getPolNumber() {
        return polNumber;
    }

    public void setPolNumber(String polNumber) {
        this.polNumber = polNumber;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getCoverDateFrom() {
        return coverDateFrom;
    }

    public void setCoverDateFrom(Date coverDateFrom) {
        this.coverDateFrom = coverDateFrom;
    }

    public Date getCoverDateTo() {
        return coverDateTo;
    }

    public void setCoverDateTo(Date coverDateTo) {
        this.coverDateTo = coverDateTo;
    }

    public Date getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(Date renewDate) {
        this.renewDate = renewDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public String getClientPin() {
        return clientPin;
    }

    public void setClientPin(String clientPin) {
        this.clientPin = clientPin;
    }

    public String getUnderwriter() {
        return underwriter;
    }

    public void setUnderwriter(String underwriter) {
        this.underwriter = underwriter;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getGrossPremium() {
        return grossPremium;
    }

    public void setGrossPremium(BigDecimal grossPremium) {
        this.grossPremium = grossPremium;
    }

    public String getTransProcessed() {
        return transProcessed;
    }

    public void setTransProcessed(String transProcessed) {
        this.transProcessed = transProcessed;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Date getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(Date dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getfClientFname() {
        return clientFname;
    }

    public void setfClientFname(String clientFname) {
        this.clientFname = clientFname;
    }

    public String getClientOtherNames() {
        return clientOtherNames;
    }

    public void setClientOtherNames(String clientOtherNames) {
        this.clientOtherNames = clientOtherNames;
    }

    public String getClientIDNo() {
        return clientIDNo;
    }

    public void setClientIDNo(String clientIDNo) {
        this.clientIDNo = clientIDNo;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhoneNo() {
        return clientPhoneNo;
    }

    public void setClientPhoneNo(String clientPhoneNo) {
        this.clientPhoneNo = clientPhoneNo;
    }

    public User getLoadedBy() {
        return loadedBy;
    }

    public void setLoadedBy(User loadedBy) {
        this.loadedBy = loadedBy;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(Date clientDOB) {
        this.clientDOB = clientDOB;
    }

    public String getClientCIF() {
        return clientCIF;
    }

    public void setClientCIF(String clientCIF) {
        this.clientCIF = clientCIF;
    }

    public BigDecimal getBankComm() {
        return bankComm;
    }

    public void setBankComm(BigDecimal bankComm) {
        this.bankComm = bankComm;
    }

    public BigDecimal getAgentComm() {
        return agentComm;
    }

    public void setAgentComm(BigDecimal agentComm) {
        this.agentComm = agentComm;
    }

    public String getSubAgnetABNo() {
        return subAgnetABNo;
    }

    public void setSubAgnetABNo(String subAgnetABNo) {
        this.subAgnetABNo = subAgnetABNo;
    }

    public String getMarketerABNo() {
        return marketerABNo;
    }

    public void setMarketerABNo(String marketerABNo) {
        this.marketerABNo = marketerABNo;
    }

    public String getLeadsABNo() {
        return leadsABNo;
    }

    public void setLeadsABNo(String leadsABNo) {
        this.leadsABNo = leadsABNo;
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

    public BigDecimal getTrainingLevy() {
        return trainingLevy;
    }

    public void setTrainingLevy(BigDecimal trainingLevy) {
        this.trainingLevy = trainingLevy;
    }

    public BigDecimal getPhFund() {
        return phFund;
    }

    public void setPhFund(BigDecimal phFund) {
        this.phFund = phFund;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getNetPremium() {
        return netPremium;
    }

    public void setNetPremium(BigDecimal netPremium) {
        this.netPremium = netPremium;
    }

    public BigDecimal getPaidPremium() {
        return paidPremium;
    }

    public void setPaidPremium(BigDecimal paidPremium) {
        this.paidPremium = paidPremium;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Long getPaidInsts() {
        return paidInsts;
    }

    public void setPaidInsts(Long paidInsts) {
        this.paidInsts = paidInsts;
    }

    public Integer getTotalInstalments() {
        return totalInstalments;
    }

    public void setTotalInstalments(Integer totalInstalments) {
        this.totalInstalments = totalInstalments;
    }
}
