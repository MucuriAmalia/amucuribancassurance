package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.setup.model.SubclassClauses;

@Entity
@Table(name="sys_brk_pol_clauses")
public class PolicyClauses {
	
	@Id
	@SequenceGenerator(name = "myPolClausesSeq",sequenceName = "policy_clauses_seq",allocationSize=1)
	@GeneratedValue(generator = "myPolClausesSeq")
	@Column(name="pol_clau_id")
	private Long polClauseId;
	
	
	@ManyToOne
	@JoinColumn(name="pol_clau_sub_code",nullable=false)
	private SubclassClauses clause;
	
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="pol_clau_pol_id")
	private PolicyTrans policy;
	
	@Column(name="pol_clau_new",length=1)
	private String newClause;
	
	@Column(name="pol_clau_header",nullable=false)
	private String clauHeading;
	
	@Column(name="pol_clau_editable",nullable=false)
	private boolean editable;
	
	@Column(name="pol_clau_wording",nullable=false)
	@Lob
	private String clauWording;

	public Long getPolClauseId() {
		return polClauseId;
	}

	public void setPolClauseId(Long polClauseId) {
		this.polClauseId = polClauseId;
	}

	public SubclassClauses getClause() {
		return clause;
	}

	public void setClause(SubclassClauses clause) {
		this.clause = clause;
	}

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public String getNewClause() {
		return newClause;
	}

	public void setNewClause(String newClause) {
		this.newClause = newClause;
	}

	public String getClauHeading() {
		return clauHeading;
	}

	public void setClauHeading(String clauHeading) {
		this.clauHeading = clauHeading;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getClauWording() {
		return clauWording;
	}

	public void setClauWording(String clauWording) {
		this.clauWording = clauWording;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clause == null) ? 0 : clause.hashCode());
		result = prime * result + ((policy == null) ? 0 : policy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyClauses other = (PolicyClauses) obj;
		if (clause == null) {
			if (other.clause != null)
				return false;
		} else if (!clause.equals(other.clause))
			return false;
		if (policy == null) {
			if (other.policy != null)
				return false;
		} else if (!policy.equals(other.policy))
			return false;
		return true;
	}

	
	
  
	
	

}
