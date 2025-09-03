package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_sub_cov_sects")
public class SubCoverTypeSections extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="scs_id")
	private Long scsId;
	
	@ManyToOne
	@JoinColumn(name="scs_sbcov_code")
	private SubclassCoverTypes subcoverType;
	
	@Column(name="scs_order")
	private Integer order;
	
	@Column(name="scs_mandatory")
	private boolean mandatory;

	@Column(name = "ss_integration")
	private String integration;
	
	@ManyToOne
	@JoinColumn(name="scs_ss_code")
	private SubclassSections subSections;

	@Column(name="sc_supports_earning",length = 1)
	private String supportsEarnings;
	@Column(name="sc_earning_type",length = 20)
	private String earningType;

	public String getSupportsEarnings() {
		return supportsEarnings;
	}

	public void setSupportsEarnings(String supportsEarnings) {
		this.supportsEarnings = supportsEarnings;
	}

	public String getEarningType() {
		return earningType;
	}

	public void setEarningType(String earningType) {
		this.earningType = earningType;
	}

	public Long getScsId() {
		return scsId;
	}

	public void setScsId(Long scsId) {
		this.scsId = scsId;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public SubclassCoverTypes getSubcoverType() {
		return subcoverType;
	}

	public void setSubcoverType(SubclassCoverTypes subcoverType) {
		this.subcoverType = subcoverType;
	}

	public SubclassSections getSubSections() {
		return subSections;
	}

	public void setSubSections(SubclassSections subSections) {
		this.subSections = subSections;
	}

	public String getIntegration() {
		return integration;
	}

	public void setIntegration(String integration) {
		this.integration = integration;
	}
}
