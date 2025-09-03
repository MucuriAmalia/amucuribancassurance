package com.brokersystems.brokerapp.trans.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

@Entity
@Table(name="sys_receipts_settlements")
public class ReceiptSettlementDetails {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="rec_settle_id")
	private Long settlementId;
	
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="rec_settle_policy")
	private PolicyTrans policy;
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="rec_settle_dr")
	private SystemTransactions debit;

	@Column(name="rec_settle_dr_ref")
	private String debitRefNo;
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="rec_settle_cr")
	private SystemTransactions credit;

	@Column(name="rec_settle_cr_ref")
	private String creditRefNo;
	
	@Column(name="rec_alloc_amt")
	private BigDecimal allocatedAmt;

	@Column(name="rec_alloc_date")
	private Date allocDate;

	@ManyToOne
	@JoinColumn(name="rec_alloc_by")
	private User alloUser;

	@Column(name="rec_drcr")
	private String drCr;

	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="rec_settle_client_cr")
	private SystemTransactions clientCredit;

	@Column(name = "rec_process_amt")
	private BigDecimal processedAmt;

	@Column(name = "rec_withdrawn")
	private String withdrawn;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "rec_withdrawn_by")
	private User userWithdrawn;

	@Column(name = "rec_withdrawn_date")
	private Date withdrawnDate;

	public String getWithdrawn() {
		return withdrawn;
	}

	public void setWithdrawn(String withdrawn) {
		this.withdrawn = withdrawn;
	}

	public User getUserWithdrawn() {
		return userWithdrawn;
	}

	public void setUserWithdrawn(User userWithdrawn) {
		this.userWithdrawn = userWithdrawn;
	}

	public Date getWithdrawnDate() {
		return withdrawnDate;
	}

	public void setWithdrawnDate(Date withdrawnDate) {
		this.withdrawnDate = withdrawnDate;
	}

	public Long getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(Long settlementId) {
		this.settlementId = settlementId;
	}

	public BigDecimal getAllocatedAmt() {
		return allocatedAmt;
	}

	public void setAllocatedAmt(BigDecimal allocatedAmt) {
		this.allocatedAmt = allocatedAmt;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public SystemTransactions getDebit() {
		return debit;
	}

	public void setDebit(SystemTransactions debit) {
		this.debit = debit;
	}

	public SystemTransactions getCredit() {
		return credit;
	}

	public void setCredit(SystemTransactions credit) {
		this.credit = credit;
	}

	public String getDebitRefNo() {
		return debitRefNo;
	}

	public void setDebitRefNo(String debitRefNo) {
		this.debitRefNo = debitRefNo;
	}

	public String getCreditRefNo() {
		return creditRefNo;
	}

	public void setCreditRefNo(String creditRefNo) {
		this.creditRefNo = creditRefNo;
	}

	public Date getAllocDate() {
		return allocDate;
	}

	public void setAllocDate(Date allocDate) {
		this.allocDate = allocDate;
	}

	public User getAlloUser() {
		return alloUser;
	}

	public void setAlloUser(User alloUser) {
		this.alloUser = alloUser;
	}

	public String getDrCr() {
		return drCr;
	}

	public void setDrCr(String drCr) {
		this.drCr = drCr;
	}

	public SystemTransactions getClientCredit() {
		return clientCredit;
	}

	public void setClientCredit(SystemTransactions clientCredit) {
		this.clientCredit = clientCredit;
	}

	public BigDecimal getProcessedAmt() {
		return processedAmt;
	}

	public void setProcessedAmt(BigDecimal processedAmt) {
		this.processedAmt = processedAmt;
	}
}
