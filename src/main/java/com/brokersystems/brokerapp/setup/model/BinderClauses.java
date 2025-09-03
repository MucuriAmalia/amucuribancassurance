package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_binder_clauses")
public class BinderClauses extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="bc_id")
	private Long clauId;
	
	@ManyToOne
	@JoinColumn(name="bc_cl_code",nullable=false)
	private SubclassClauses clause;
	
	@ManyToOne
	@JoinColumn(name="bc_bd_code",nullable=false)
	private BinderDetails binderDet;

	@Column(name="bc_mandatory",length = 1)
	private String mandatory;

	@Column(name="bc_clau_wording")
	@Lob
	private String clauWording;

	@Column(name="bc_clau_header")
	private String clauHeading;

	@Column(name="bc_clau_editable",length = 1)
	private String editable;

	public Long getClauId() {
		return clauId;
	}

	public void setClauId(Long clauId) {
		this.clauId = clauId;
	}

	public SubclassClauses getClause() {
		return clause;
	}

	public void setClause(SubclassClauses clause) {
		this.clause = clause;
	}

	public BinderDetails getBinderDet() {
		return binderDet;
	}

	public void setBinderDet(BinderDetails binderDet) {
		this.binderDet = binderDet;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getClauWording() {
		return clauWording;
	}

	public void setClauWording(String clauWording) {
		this.clauWording = clauWording;
	}

	public String getClauHeading() {
		return clauHeading;
	}

	public void setClauHeading(String clauHeading) {
		this.clauHeading = clauHeading;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}
}
