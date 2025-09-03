package com.brokersystems.brokerapp.certs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Entity
@Table(name="sys_brk_cert_types")
public class CertTypes {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cert_id")
	private Long certId;
	
	@Column(name="cert_sht_desc",nullable=false,unique=true)
	private String certShtDesc;
	
	@Column(name="cert_desc",nullable=false)
	private String certDesc;
	
	@Column(name="cert_prefix",nullable=false,unique=true)
	private String certPrefix;

	@Column(name="cert_template")
	private String certTemplate;

	@Column(name="cert_min_cap")
	private Long minCapacity;

	@Column(name="cert_max_cap")
	private Long maxCapacity;

	@Column(name="cert_reorder_level")
	private Long reorderLevel;



	public Long getCertId() {
		return certId;
	}

	public void setCertId(Long certId) {
		this.certId = certId;
	}

	public String getCertShtDesc() {
		return certShtDesc;
	}

	public void setCertShtDesc(String certShtDesc) {
		this.certShtDesc = certShtDesc;
	}

	public String getCertDesc() {
		return certDesc;
	}

	public void setCertDesc(String certDesc) {
		this.certDesc = certDesc;
	}

	public String getCertPrefix() {
		return certPrefix;
	}

	public void setCertPrefix(String certPrefix) {
		this.certPrefix = certPrefix;
	}

	public String getCertTemplate() {
		return certTemplate;
	}

	public void setCertTemplate(String certTemplate) {
		this.certTemplate = certTemplate;
	}

	public Long getMinCapacity() {
		return minCapacity;
	}

	public void setMinCapacity(Long minCapacity) {
		this.minCapacity = minCapacity;
	}

	public Long getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(Long maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public Long getReorderLevel() {
		return reorderLevel;
	}

	public void setReorderLevel(Long reorderLevel) {
		this.reorderLevel = reorderLevel;
	}}
	
	
	
	
	


