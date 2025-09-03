package com.brokersystems.brokerapp.setup.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_covertypes")
public class CoverTypesDef extends AuditBaseEntity{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cov_id")
	private Long covId;

	@Column(name="cov_active",nullable=false)
	private boolean active;

	@Column(name="cov_sht_desc",nullable=false)
	private String covShtDesc;

	@Transient
	private String subclassName;

	@Column(name="cov_desc",nullable=false)
	private String covName;

	@Column(name="cov_type_appl",length = 1)
	private String typeApplicable;

	@Column(name="cov_types",length = 2000)
	private String types;

	@Transient
	private Long detId;
	@Transient
	private Long binId;

	@Transient
	private Integer installmentsNo;

	@Transient
	private String distribution;

	private BigDecimal minSi;
	@Transient
	private BigDecimal commRate;

	@XmlTransient
	 @JsonIgnore
	@OneToMany(mappedBy="coverTypes")
	private List<SubclassCoverTypes> coverTypes;

	@ManyToOne
	@JoinColumn(name = "cov_class_id")
	private ClassesDef classesDef;

	public Long getBinId() {
		return binId;
	}

	public void setBinId(Long binId) {
		this.binId = binId;
	}

	public ClassesDef getClassesDef() {
		return classesDef;
	}

	public void setClassesDef(ClassesDef classesDef) {
		this.classesDef = classesDef;
	}

	public BigDecimal getCommRate() {
		return commRate;
	}

	public void setCommRate(BigDecimal commRate) {
		this.commRate = commRate;
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

	public Long getCovId() {
		return covId;
	}

	public void setCovId(Long covId) {
		this.covId = covId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCovShtDesc() {
		return covShtDesc;
	}

	public void setCovShtDesc(String covShtDesc) {
		this.covShtDesc = covShtDesc;
	}

	public String getCovName() {
		return covName;
	}

	public void setCovName(String covName) {
		this.covName = covName;
	}

	public BigDecimal getMinSi() {
		return minSi;
	}

	public void setMinSi(BigDecimal minSi) {
		this.minSi = minSi;
	}

	public List<SubclassCoverTypes> getCoverTypes() {
		return coverTypes;
	}

	public void setCoverTypes(List<SubclassCoverTypes> coverTypes) {
		this.coverTypes = coverTypes;
	}

	public Long getDetId() {
		return detId;
	}

	public void setDetId(Long detId) {
		this.detId = detId;
	}


	public String getTypeApplicable() {
		return typeApplicable;
	}

	public void setTypeApplicable(String typeApplicable) {
		this.typeApplicable = typeApplicable;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getSubclassName() {
		return subclassName;
	}

	public void setSubclassName(String subclassName) {
		this.subclassName = subclassName;
	}
}
