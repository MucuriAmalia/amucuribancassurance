package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="sys_brk_temp_transactions",indexes = {@Index(name = "trans_ref_no_idx",columnList = "trans_ref_no",unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SystemTransactionsTemp {
	
	@Id
	@SequenceGenerator(name = "tempTransSeq",sequenceName = "tempTransSeq",allocationSize=1)
	@GeneratedValue(generator = "tempTransSeq")
	@Column(name="trans_no")
	private Long tempTransno;

//	@Id
//	@GeneratedValue(generator = "UUID")
//	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
//	@Column(name = "trans_no", updatable = false, nullable = false)
//	private String transno;

	@Column(name="trans_date")
	private Date transDate;
	
	@Column(name="trans_ref_no")
	private String refNo;

	@Column(name="trans_other_ref")
	private String otherRef;
	
	@Column(name="trans_type")
	private String transType;
	
	@Column(name="trans_dc")
	private String transdc;
	
	@Column(name="trans_control_acc")
	private String controlAcc;
	
	@Column(name="trans_clnt_type")
	private String clientType;
	
	@ManyToOne
	@JoinColumn(name="trans_clnt_code")
	private ClientDef client;
	
	@ManyToOne
	@JoinColumn(name="trans_agent_code")
	private AccountDef agent;
	
	@Column(name="trans_amount")
	private BigDecimal amount;
	
	@Column(name="trans_balance")
	private BigDecimal balance;
	
	@Column(name="trans_net_amt")
	private BigDecimal netAmount;
	
	@Column(name="trans_settle_amt")
	private BigDecimal settleAmt;

	@Column(name="trans_settle_temp_amt")
	private BigDecimal tempSettleAmt;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="trans_posted_by")
	private User postedUser;

	@Column(name="trans_posted_dt")
	private Date postedDate;
	
	@ManyToOne
	@JoinColumn(name="trans_pol_id")
	private PolicyTrans policy;
	
	@Column(name="trans_narrations")
	private String narrations;
	
	@Column(name="trans_authorised")
	private String authorised;
	
	@Column(name="trans_auth_date")
	private Date authDate;
	
	@Column(name="trans_user_auth")
	private String userAuth;
	
	@Column(name="trans_origin")
	private String origin;
	
	@ManyToOne
	@JoinColumn(name="trans_curr_id",nullable=false)
	private Currencies currency;
	
	@Column(name="trans_curr_rate")
	private BigDecimal currRate;
	
	@Column(name="trans_sd")
	private BigDecimal sd;
	
	@Column(name="trans_tl")
	private BigDecimal tl;
	
	@Column(name="trans_whtx")
	private BigDecimal whtx;
	
	@Column(name="trans_comm")
	private BigDecimal commission;
	
	@Column(name="trans_phfund")
	private BigDecimal phfund;
	
	@Column(name="trans_extras")
	private BigDecimal extras;

	@Column(name="trans_service_charge")
	private BigDecimal serviceCharge;

	@Column(name="trans_issue_card_fee")
	private BigDecimal issueCardFee;

	@Column(name="trans_reissue_card_fee")
	private BigDecimal reIssueCardFee;

	@Column(name="trans_vat_amount")
	private BigDecimal vatAmount;
	
	@ManyToOne
	@JoinColumn(name="trans_brn_id")
	private OrgBranch branch;

	@Column(name="trans_payee")
	private String payeeName;


    public Long getTempTransno() {
		return tempTransno;
	}

	public void setTempTransno(Long tempTransno) {
		this.tempTransno = tempTransno;
	}

	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getOtherRef() {
		return otherRef;
	}

	public void setOtherRef(String otherRef) {
		this.otherRef = otherRef;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransdc() {
		return transdc;
	}

	public void setTransdc(String transdc) {
		this.transdc = transdc;
	}

	public String getControlAcc() {
		return controlAcc;
	}

	public void setControlAcc(String controlAcc) {
		this.controlAcc = controlAcc;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public String getNarrations() {
		return narrations;
	}

	public void setNarrations(String narrations) {
		this.narrations = narrations;
	}

	public String getAuthorised() {
		return authorised;
	}

	public void setAuthorised(String authorised) {
		this.authorised = authorised;
	}

	public Date getAuthDate() {
		return authDate;
	}

	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}

	public String getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(String userAuth) {
		this.userAuth = userAuth;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Currencies getCurrency() {
		return currency;
	}

	public void setCurrency(Currencies currency) {
		this.currency = currency;
	}

	public BigDecimal getCurrRate() {
		return currRate;
	}

	public void setCurrRate(BigDecimal currRate) {
		this.currRate = currRate;
	}

	public BigDecimal getSd() {
		return sd;
	}

	public void setSd(BigDecimal sd) {
		this.sd = sd;
	}

	public BigDecimal getTl() {
		return tl;
	}

	public void setTl(BigDecimal tl) {
		this.tl = tl;
	}

	public BigDecimal getWhtx() {
		return whtx;
	}

	public void setWhtx(BigDecimal whtx) {
		this.whtx = whtx;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getPhfund() {
		return phfund;
	}

	public void setPhfund(BigDecimal phfund) {
		this.phfund = phfund;
	}

	public OrgBranch getBranch() {
		return branch;
	}

	public void setBranch(OrgBranch branch) {
		this.branch = branch;
	}

	public BigDecimal getSettleAmt() {
		return settleAmt;
	}

	public void setSettleAmt(BigDecimal settleAmt) {
		this.settleAmt = settleAmt;
	}

	public BigDecimal getExtras() {
		return extras;
	}

	public void setExtras(BigDecimal extras) {
		this.extras = extras;
	}

	public ClientDef getClient() {
		return client;
	}

	public void setClient(ClientDef client) {
		this.client = client;
	}

	public AccountDef getAgent() {
		return agent;
	}

	public void setAgent(AccountDef agent) {
		this.agent = agent;
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

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

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

	public BigDecimal getReIssueCardFee() {
		return reIssueCardFee;
	}

	public void setReIssueCardFee(BigDecimal reIssueCardFee) {
		this.reIssueCardFee = reIssueCardFee;
	}

	public BigDecimal getTempSettleAmt() {
		return tempSettleAmt;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public void setTempSettleAmt(BigDecimal tempSettleAmt) {
		this.tempSettleAmt = tempSettleAmt;
	}


}
