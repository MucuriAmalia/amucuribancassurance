package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_act_risks")
public class PolicyActiveRisks {
	
	@Id
	@SequenceGenerator(name = "myActiveRisksSeq",sequenceName = "active_risks_seq",allocationSize=1)
	@GeneratedValue(generator = "myActiveRisksSeq")
	@Column(name="ar_id")
	private Long arId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ar_risk_id")
	private RiskTrans risk;
	
	@Column(name="risk_code")
	private Long riskIdentifier;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ar_pol_id")
	private PolicyTrans policy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ar_prev_risk_id")
	private RiskTrans prevRisk;

	@Column(name="risk_status")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getArId() {
		return arId;
	}

	public void setArId(Long arId) {
		this.arId = arId;
	}

	public RiskTrans getRisk() {
		return risk;
	}

	public void setRisk(RiskTrans risk) {
		this.risk = risk;
	}

	public Long getRiskIdentifier() {
		return riskIdentifier;
	}

	public void setRiskIdentifier(Long riskIdentifier) {
		this.riskIdentifier = riskIdentifier;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public RiskTrans getPrevRisk() {
		return prevRisk;
	}

	public void setPrevRisk(RiskTrans prevRisk) {
		this.prevRisk = prevRisk;
	}
	
}
