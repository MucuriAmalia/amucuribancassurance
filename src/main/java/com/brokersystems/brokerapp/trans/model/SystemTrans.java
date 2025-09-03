package com.brokersystems.brokerapp.trans.model;

import java.util.Date;

import javax.persistence.*;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

@Entity
@Table(name="sys_brk_trans")
public class SystemTrans {
	
	@Id
	@SequenceGenerator(name = "brkTransSeq",sequenceName = "sys_trans_seq",allocationSize=1)
	@GeneratedValue(generator = "brkTransSeq")
	@Column(name="tran_no")
	private Long transNo;
	
	@Column(name="tran_code",nullable=false)
	private String transCode;
	
	@ManyToOne
	@JoinColumn(name="tran_done_by",nullable=false)
	private User doneBy;
	
	@Column(name="tran_done_date")
	private Date doneDate;
	
	@Column(name="tran_level")
	private String transLevel;
	
	@ManyToOne
	@JoinColumn(name="tran_policy")
	private PolicyTrans policy;
	
	@Column(name="tran_authorised")
	private String transAuthorised;
	
	@ManyToOne
	@JoinColumn(name="tran_auth_by")
	private User authBy;
	
	@Column(name="tran_auth_date")
	private Date authDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="tran_claim")
	private ClaimBookings claim;

	public ClaimBookings getClaim() {
		return claim;
	}

	public void setClaim(ClaimBookings claim) {
		this.claim = claim;
	}

	public Long getTransNo() {
		return transNo;
	}

	public void setTransNo(Long transNo) {
		this.transNo = transNo;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public User getDoneBy() {
		return doneBy;
	}

	public void setDoneBy(User doneBy) {
		this.doneBy = doneBy;
	}

	public Date getDoneDate() {
		return doneDate;
	}

	public void setDoneDate(Date doneDate) {
		this.doneDate = doneDate;
	}

	public String getTransLevel() {
		return transLevel;
	}

	public void setTransLevel(String transLevel) {
		this.transLevel = transLevel;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public String getTransAuthorised() {
		return transAuthorised;
	}

	public void setTransAuthorised(String transAuthorised) {
		this.transAuthorised = transAuthorised;
	}

	public User getAuthBy() {
		return authBy;
	}

	public void setAuthBy(User authBy) {
		this.authBy = authBy;
	}

	public Date getAuthDate() {
		return authDate;
	}

	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}
	
	
	
	
	
	
	

}
