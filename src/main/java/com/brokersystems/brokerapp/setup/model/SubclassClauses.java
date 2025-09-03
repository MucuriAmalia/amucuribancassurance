package com.brokersystems.brokerapp.setup.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_sub_clauses")
public class SubclassClauses  extends AuditBaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="subcl_cl_id")
	private Long clauId;
	
	@ManyToOne
	@JoinColumn(name="subcl_sub_code",nullable=false)
	private SubClassDef subclass;
	
	@ManyToOne
	@JoinColumn(name="subcl_cl_code",nullable=false)
	private ClausesDef clause;

	@Column(name="subcl_cl_mandatory")
	private boolean mandatory;
	
	@XmlTransient
	 @JsonIgnore
	@OneToMany(mappedBy="clause")
	private List<BinderClauses> binderClauses;

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy="clausesDef")
	private List<SectionLimits> sectionLimits;


	public List<SectionLimits> getSectionLimits() {
		return sectionLimits;
	}

	public void setSectionLimits(List<SectionLimits> sectionLimits) {
		this.sectionLimits = sectionLimits;
	}

	public Long getClauId() {
		return clauId;
	}

	public void setClauId(Long clauId) {
		this.clauId = clauId;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public ClausesDef getClause() {
		return clause;
	}

	public void setClause(ClausesDef clause) {
		this.clause = clause;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public List<BinderClauses> getBinderClauses() {
		return binderClauses;
	}

	public void setBinderClauses(List<BinderClauses> binderClauses) {
		this.binderClauses = binderClauses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clauId == null) ? 0 : clauId.hashCode());
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
		SubclassClauses other = (SubclassClauses) obj;
		if (clauId == null) {
			if (other.clauId != null)
				return false;
		} else if (!clauId.equals(other.clauId))
			return false;
		return true;
	}
	
	

}
