package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_binder_det",uniqueConstraints={@UniqueConstraint(columnNames={"bdet_bin_code","bdet_sub_covt_code"})})
public class BinderDetails extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="bdet_id")
	private Long detId;

	@ManyToOne
	@JoinColumn(name="bdet_bin_code",nullable=false)
	private BindersDef binder;

	@Column(name = "bin_max_exposure")
	private BigDecimal maxExposure;
	@ManyToOne
	@JoinColumn(name="bdet_sub_covt_code",nullable=false)
	private SubclassCoverTypes subCoverTypes;

	@Column(name="bdet_cover_summary",length = 3000)
	private String remarks;

	@Column(name="bdet_min_prem")
	private BigDecimal minPrem;

	@Column(name="sc_single_section", length = 1)
	private String singleSectionCover;

	@Column(name="sc_limits_allowed",length = 1)
	private String limitsAllowed;

	@Column(name="sc_primary",length = 1)
	private String primary;

	@Column(name = "sc_installments")
	private Integer installmentsNo;

	@Column(name = "sc_distributions")
	private String distribution;

	public BigDecimal getMaxExposure() {
		return maxExposure;
	}

	public void setMaxExposure(BigDecimal maxExposure) {
		this.maxExposure = maxExposure;
	}

	public Long getDetId() {
		return detId;
	}

	public void setDetId(Long detId) {
		this.detId = detId;
	}

	public BindersDef getBinder() {
		return binder;
	}

	public void setBinder(BindersDef binder) {
		this.binder = binder;
	}

	public SubclassCoverTypes getSubCoverTypes() {
		return subCoverTypes;
	}

	public void setSubCoverTypes(SubclassCoverTypes subCoverTypes) {
		this.subCoverTypes = subCoverTypes;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getMinPrem() {
		return minPrem;
	}

	public void setMinPrem(BigDecimal minPrem) {
		this.minPrem = minPrem;
	}

	public String getSingleSectionCover() {
		return singleSectionCover;
	}

	public void setSingleSectionCover(String singleSectionCover) {
		this.singleSectionCover = singleSectionCover;
	}

	public String getLimitsAllowed() {
		return limitsAllowed;
	}

	public void setLimitsAllowed(String limitsAllowed) {
		this.limitsAllowed = limitsAllowed;
	}

	public String getPrimary() {
		return primary;
	}

	public void setPrimary(String primary) {
		this.primary = primary;
	}

	public Integer getInstallmentsNo() {
		return installmentsNo;
	}

	public void setInstallmentsNo(Integer installmentsNo) {
		this.installmentsNo = installmentsNo;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
}
