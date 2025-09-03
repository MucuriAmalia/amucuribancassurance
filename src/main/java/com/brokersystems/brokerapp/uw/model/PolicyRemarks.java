package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.setup.model.EndorsementRemarks;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_pol_remarks")
public class PolicyRemarks {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pr_id")
	private Long remarksId;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="pr_policy_id",nullable=false)
	private PolicyTrans policy;
	
	@Column(name="pr_remarks",nullable=false)
	@Lob
	private String polRemarks;

	@Transient
	private String remarks;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="pr_remark_id",nullable=false)
	private EndorsementRemarks endRemarks;


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getRemarksId() {
		return remarksId;
	}

	public void setRemarksId(Long remarksId) {
		this.remarksId = remarksId;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public String getPolRemarks() {
		return polRemarks;
	}

	public void setPolRemarks(String polRemarks) {
		this.polRemarks = polRemarks;
	}

	public EndorsementRemarks getEndRemarks() {
		return endRemarks;
	}

	public void setEndRemarks(EndorsementRemarks endRemarks) {
		this.endRemarks = endRemarks;
	}

	
	
	
	
	

}
