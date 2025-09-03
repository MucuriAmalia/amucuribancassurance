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
@Table(name="sys_brk_sub_sections")
public class SubclassSections extends AuditBaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ss_id")
	private Long ssId;
	
	@Column(name="ss_active")
	private boolean active;
	
	@ManyToOne
	@JoinColumn(name="ss_sc_code",nullable = false)
	private SectionsDef section;
	
	@ManyToOne
	@JoinColumn(name="ss_sub_code")
	private SubClassDef subclass;
	
	@Column(name="ss_declaration")
	private boolean declaration;

	@Column(name = "ss_refundable")
	private String refundable;
	
	@XmlTransient
	 @JsonIgnore
	@OneToMany(mappedBy="subSections")
	private List<SubCoverTypeSections> subcovsections;


	@Column(name = "ss_compute_commission",length = 1)
	private String computeCommission;

	public String getComputeCommission() {
		return computeCommission;
	}

	public void setComputeCommission(String computeCommission) {
		this.computeCommission = computeCommission;
	}

	public Long getSsId() {
		return ssId;
	}

	public void setSsId(Long ssId) {
		this.ssId = ssId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SectionsDef getSection() {
		return section;
	}

	public void setSection(SectionsDef section) {
		this.section = section;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public boolean isDeclaration() {
		return declaration;
	}

	public void setDeclaration(boolean declaration) {
		this.declaration = declaration;
	}

	public List<SubCoverTypeSections> getSubcovsections() {
		return subcovsections;
	}

	public void setSubcovsections(List<SubCoverTypeSections> subcovsections) {
		this.subcovsections = subcovsections;
	}

	public String getRefundable() {
		return refundable;
	}

	public void setRefundable(String refundable) {
		this.refundable = refundable;
	}
}
