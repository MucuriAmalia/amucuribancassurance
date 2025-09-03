package com.brokersystems.brokerapp.setup.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_sub_covertypes")
public class SubclassCoverTypes extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="sc_id")
	private Long scId;
	
	@ManyToOne
	@JoinColumn(name="sc_sub_code")
	private SubClassDef subclass;
	
	@ManyToOne
	@JoinColumn(name="sc_cov_code")
	private CoverTypesDef coverTypes;
	
	@Column(name="sc_def_cover")
	private boolean defaultCoverType;
	
	@Column(name="sc_min_prem")
	private BigDecimal minPrem;



	public Long getScId() {
		return scId;
	}

	public void setScId(Long scId) {
		this.scId = scId;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public CoverTypesDef getCoverTypes() {
		return coverTypes;
	}

	public void setCoverTypes(CoverTypesDef coverTypes) {
		this.coverTypes = coverTypes;
	}

	public boolean isDefaultCoverType() {
		return defaultCoverType;
	}

	public void setDefaultCoverType(boolean defaultCoverType) {
		this.defaultCoverType = defaultCoverType;
	}

	public BigDecimal getMinPrem() {
		return minPrem;
	}

	public void setMinPrem(BigDecimal minPrem) {
		this.minPrem = minPrem;
	}

}
