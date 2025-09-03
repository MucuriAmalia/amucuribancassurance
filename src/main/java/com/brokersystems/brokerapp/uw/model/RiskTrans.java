package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.quotes.model.QuoteRiskTrans;
import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import com.brokersystems.brokerapp.setup.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_risks")
public class RiskTrans {
	
	@Id
	@SequenceGenerator(name = "myRiskSeq",sequenceName = "risk_seq",allocationSize=1)
	@GeneratedValue(generator = "myRiskSeq")
	@Column(name="risk_id")
	private Long riskId;
	
	@Column(name="risk_sht_desc",unique = true)
	private String riskShtDesc;
	
	@Column(name="risk_desc")
	private String riskDesc;
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_pol_id")
	private PolicyTrans policy;


	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_pol_bind_id",nullable = true)
	private PolicyBinders policyBinders;


	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_insured_id",nullable=false)
	private ClientDef insured;

	
	@Column(name="risk_wef_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date wefDate;
	
	@Column(name="risk_wet_date",nullable=false)
	@Temporal(TemporalType.DATE)
	private Date wetDate;
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_binder_id",nullable=false)
	private BindersDef binder;

	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_subclass_id",nullable=false)
	private SubClassDef subclass;

	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_cover_id",nullable=false)
	private CoverTypesDef covertype;

	
	@Column(name="risk_sum_insur_amt")
	private BigDecimal sumInsured;
	
	@Column(name="risk_basic_premium")
	private BigDecimal premium;

	@Column(name="risk_stamp_duty")
	private BigDecimal stampDuty;

	@Column(name="risk_ph_fund")
	private BigDecimal phfFund;

	@Column(name="risk_tl")
	private BigDecimal trainingLevy;
	
	@Column(name="risk_whtx")
	private BigDecimal whtax;
	
	@Column(name="risk_extras")
	private BigDecimal extras;
	
	@Column(name="risk_net_premium")
	private BigDecimal netpremium;

	@Column(name="risk_future_prem")
	private BigDecimal futurePrem;
	
	@Column(name="risk_comm_rate")
	private BigDecimal commRate;

	@Column(name="risk_subagent_comm_rate")
	private BigDecimal subAgentCommRate;

	@Column(name="risk_marketer_comm_rate")
	private BigDecimal marketerCommRate;

	@Column(name="risk_introducer_comm_rate")
	private BigDecimal introducerCommRate;

	@Column(name="risk_quartely_prem")
	private BigDecimal quartelyPremium;

	@Column(name="risk_semi_ann_prem")
	private BigDecimal semiAnnualPremium;

	@Column(name="risk_annual_prem")
	private BigDecimal annualPremium;

	@Column(name="risk_single_prem")
	private BigDecimal singlePremium;

	@Column(name="risk_tax_relief")
	private BigDecimal taxRelief;
	
	@Column(name="risk_comm_amt")
	private BigDecimal commAmt;

	@Column(name="risk_subagent_comm_amt")
	private BigDecimal subAgentComm;

	@Column(name="risk_marketer_comm_amt")
	private BigDecimal marketerAgentComm;

	@Column(name="risk_introducer_comm_amt")
	private BigDecimal introducerAgentComm;

	@Column(name="risk_prorata")
	private String prorata;
	
	@Column(name="risk_free_limit")
	private BigDecimal freeLimit;
	
	@Column(name="risk_cal_premium")
	private BigDecimal calcPremium;

	@Column(name = "risk_investment_amt")
	private BigDecimal investment;

	@Column(name = "risk_topup_amt")
	private BigDecimal topUp;

	@Column(name="risk_but_charge_prem")
	private BigDecimal butchargePrem;

	@Column(name="risk_installment_no")
	private Long installmentNo;

	@Column(name="risk_installment_percentage")
	private String installmentPerc;

	@Column(name = "risk_install_amount")
	private BigDecimal installAmount;

	@Column(name = "risk_tot_perc")
	private BigDecimal totalPercentage;

	@Column(name = "risk_prev_percent")
	private BigDecimal prevPercentage;

	private BigDecimal computePremium;

	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_binder_det_id",nullable=false)
	private BinderDetails binderDetails;

	@XmlTransient
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="risk_quot_risk_id")
	private QuoteRiskTrans quoteRisk;

	@JsonIgnore
	@OneToMany(mappedBy="risk")
	private List<SectionTrans> sectionTrans;

	@JsonIgnore
	@OneToMany(mappedBy="risk")
	private List<RiskDocs> riskDocses;
	
	@Column(name="risk_code")
	private Long riskIdentifier;
	
	@Column(name="risk_uw_yr")
	private Integer uwYear;

	@Column(name="risk_working_age")
	private Integer workingAge;
	
	@Column(name="risk_trans_type")
	private String transType;

	@Column(name="risk_auto_gen_cert")
	private String autogenCert;

	@Column(name="risk_compute_type")
	private String computeType;

	@XmlTransient
	 @JsonIgnore
	@OneToMany(mappedBy="risk")
	private List<PolicyActiveRisks> activeRisks;

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy="risk")
	private List<ScheduleTrans> scheduleTrans;

	@Column(name = "risk_status")
	private Boolean pending;

	public String getNonMaskedValue() {
		return nonMaskedValue;
	}

	public void setNonMaskedValue(String nonMaskedValue) {
		this.nonMaskedValue = nonMaskedValue;
	}

	@Column(name="risk_non_masked")
	private String nonMaskedValue;

//	public boolean isPending() {
//		return pending;
//	}
//
//	public void setPending(boolean status) {
//		this.pending = status;
//	}


	public BigDecimal getSubAgentCommRate() {
		return subAgentCommRate;
	}

	public void setSubAgentCommRate(BigDecimal subAgentCommRate) {
		this.subAgentCommRate = subAgentCommRate;
	}

	public BigDecimal getMarketerCommRate() {
		return marketerCommRate;
	}

	public void setMarketerCommRate(BigDecimal marketerCommRate) {
		this.marketerCommRate = marketerCommRate;
	}

	public BigDecimal getIntroducerCommRate() {
		return introducerCommRate;
	}

	public void setIntroducerCommRate(BigDecimal introducerCommRate) {
		this.introducerCommRate = introducerCommRate;
	}

	public BigDecimal getMarketerAgentComm() {
		return marketerAgentComm;
	}

	public void setMarketerAgentComm(BigDecimal marketerComm) {
		this.marketerAgentComm = marketerComm;
	}

	public BigDecimal getIntroducerAgentComm() {
		return introducerAgentComm;
	}

	public void setIntroducerAgentComm(BigDecimal introducerComm) {
		this.introducerAgentComm = introducerComm;
	}

	public BigDecimal getTaxRelief() {
		return taxRelief;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}

	public void setTaxRelief(BigDecimal taxRelief) {
		this.taxRelief = taxRelief;
	}

	public BigDecimal getComputePremium() {
		return computePremium;
	}

	public void setComputePremium(BigDecimal computePremium) {
		this.computePremium = computePremium;
	}

	public BigDecimal getTotalPercentage() {
        return totalPercentage;
    }

    public void setTotalPercentage(BigDecimal totalPercentage) {
        this.totalPercentage = totalPercentage;
    }

	public BigDecimal getPrevPercentage() {
		return prevPercentage;
	}

	public void setPrevPercentage(BigDecimal prevPercentage) {
		this.prevPercentage = prevPercentage;
	}

	public Integer getWorkingAge() {
		return workingAge;
	}

	public void setWorkingAge(Integer workingAge) {
		this.workingAge = workingAge;
	}

	public List<RiskDocs> getRiskDocses() {
		return riskDocses;
	}

	public void setRiskDocses(List<RiskDocs> riskDocses) {
		this.riskDocses = riskDocses;
	}

	public BigDecimal getInstallAmount() {
		return installAmount;
	}

	public void setInstallAmount(BigDecimal installAmount) {
		this.installAmount = installAmount;
	}

	@JsonIgnore
	@OneToMany(mappedBy="risk")
	private List<RiskInterestedParties> interestParties;

	public Long getRiskId() {
		return riskId;
	}

	public void setRiskId(Long riskId) {
		this.riskId = riskId;
	}

	public Long getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(Long installmentNo) {
		this.installmentNo = installmentNo;
	}

	public String getInstallmentPerc() {
		return installmentPerc;
	}

	public void setInstallmentPerc(String installmentPerc) {
		this.installmentPerc = installmentPerc;
	}

	public String getRiskShtDesc() {
		return riskShtDesc;
	}

	public void setRiskShtDesc(String riskShtDesc) {
		this.riskShtDesc = riskShtDesc;
	}

	public String getRiskDesc() {
		return riskDesc;
	}

	public void setRiskDesc(String riskDesc) {
		this.riskDesc = riskDesc;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public ClientDef getInsured() {
		return insured;
	}

	public void setInsured(ClientDef insured) {
		this.insured = insured;
	}

	public BigDecimal getQuartelyPremium() {
		return quartelyPremium;
	}

	public void setQuartelyPremium(BigDecimal quartelyPremium) {
		this.quartelyPremium = quartelyPremium;
	}

	public BigDecimal getSemiAnnualPremium() {
		return semiAnnualPremium;
	}

	public void setSemiAnnualPremium(BigDecimal semiAnnualPremium) {
		this.semiAnnualPremium = semiAnnualPremium;
	}

	public BigDecimal getAnnualPremium() {
		return annualPremium;
	}

	public void setAnnualPremium(BigDecimal annualPremium) {
		this.annualPremium = annualPremium;
	}

	public BigDecimal getSinglePremium() {
		return singlePremium;
	}

	public void setSinglePremium(BigDecimal singlePremium) {
		this.singlePremium = singlePremium;
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

	public BindersDef getBinder() {
		return binder;
	}

	public void setBinder(BindersDef binder) {
		this.binder = binder;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public CoverTypesDef getCovertype() {
		return covertype;
	}

	public void setCovertype(CoverTypesDef covertype) {
		this.covertype = covertype;
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



	public BigDecimal getFuturePrem() {
		return futurePrem;
	}

	public void setFuturePrem(BigDecimal futurePrem) {
		this.futurePrem = futurePrem;
	}

	public BigDecimal getCommRate() {
		return commRate;
	}

	public void setCommRate(BigDecimal commRate) {
		this.commRate = commRate;
	}

	public BigDecimal getCommAmt() {
		return commAmt;
	}

	public void setCommAmt(BigDecimal commAmt) {
		this.commAmt = commAmt;
	}

	public String getProrata() {
		return prorata;
	}

	public void setProrata(String prorata) {
		this.prorata = prorata;
	}

	public List<SectionTrans> getSectionTrans() {
		return sectionTrans;
	}

	public void setSectionTrans(List<SectionTrans> sectionTrans) {
		this.sectionTrans = sectionTrans;
	}

	public BinderDetails getBinderDetails() {
		return binderDetails;
	}

	public void setBinderDetails(BinderDetails binderDetails) {
		this.binderDetails = binderDetails;
	}

	public BigDecimal getStampDuty() {
		return stampDuty;
	}

	public void setStampDuty(BigDecimal stampDuty) {
		this.stampDuty = stampDuty;
	}

	public BigDecimal getPhfFund() {
		return phfFund;
	}

	public void setPhfFund(BigDecimal phfFund) {
		this.phfFund = phfFund;
	}

	public BigDecimal getTrainingLevy() {
		return trainingLevy;
	}

	public void setTrainingLevy(BigDecimal trainingLevy) {
		this.trainingLevy = trainingLevy;
	}

	public BigDecimal getWhtax() {
		return whtax;
	}

	public void setWhtax(BigDecimal whtax) {
		this.whtax = whtax;
	}

	public BigDecimal getExtras() {
		return extras;
	}

	public void setExtras(BigDecimal extras) {
		this.extras = extras;
	}

	public BigDecimal getNetpremium() {
		return netpremium;
	}

	public void setNetpremium(BigDecimal netpremium) {
		this.netpremium = netpremium;
	}

	public BigDecimal getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(BigDecimal freeLimit) {
		this.freeLimit = freeLimit;
	}

	public BigDecimal getCalcPremium() {
		return calcPremium;
	}

	public void setCalcPremium(BigDecimal calcPremium) {
		this.calcPremium = calcPremium;
	}

	public Integer getUwYear() {
		return uwYear;
	}

	public void setUwYear(Integer uwYear) {
		this.uwYear = uwYear;
	}

	public Long getRiskIdentifier() {
		return riskIdentifier;
	}

	public void setRiskIdentifier(Long riskIdentifier) {
		this.riskIdentifier = riskIdentifier;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public List<PolicyActiveRisks> getActiveRisks() {
		return activeRisks;
	}

	public void setActiveRisks(List<PolicyActiveRisks> activeRisks) {
		this.activeRisks = activeRisks;
	}

	public String getAutogenCert() {
		return autogenCert;
	}

	public void setAutogenCert(String autogenCert) {
		this.autogenCert = autogenCert;
	}

	public BigDecimal getButchargePrem() {
		return butchargePrem;
	}

	public void setButchargePrem(BigDecimal butchargePrem) {
		this.butchargePrem = butchargePrem;
	}

	public BigDecimal getSubAgentComm() {
		return subAgentComm;
	}

	public void setSubAgentComm(BigDecimal subAgentComm) {
		this.subAgentComm = subAgentComm;
	}

	public QuoteRiskTrans getQuoteRisk() {
		return quoteRisk;
	}

	public void setQuoteRisk(QuoteRiskTrans quoteRisk) {
		this.quoteRisk = quoteRisk;
	}


	public String getComputeType() {
		return computeType;
	}

	public void setComputeType(String computeType) {
		this.computeType = computeType;
	}

	public List<RiskInterestedParties> getInterestParties() {
		return interestParties;
	}

	public void setInterestParties(List<RiskInterestedParties> interestParties) {
		this.interestParties = interestParties;
	}

	public PolicyBinders getPolicyBinders() {
		return policyBinders;
	}

	public void setPolicyBinders(PolicyBinders policyBinders) {
		this.policyBinders = policyBinders;
	}

	public List<ScheduleTrans> getScheduleTrans() {
		return scheduleTrans;
	}

	public void setScheduleTrans(List<ScheduleTrans> scheduleTrans) {
		this.scheduleTrans = scheduleTrans;
	}

	public BigDecimal getInvestment() {
		return investment;
	}

	public void setInvestment(BigDecimal investment) {
		this.investment = investment;
	}

	public BigDecimal getTopUp() {
		return topUp;
	}

	public void setTopUp(BigDecimal topUp) {
		this.topUp = topUp;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RiskTrans riskTrans = (RiskTrans) o;

		return riskId.equals(riskTrans.riskId);

	}

	@Override
	public int hashCode() {
		return riskId.hashCode();
	}
}
