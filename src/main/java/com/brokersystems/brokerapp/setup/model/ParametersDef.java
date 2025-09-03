package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_parameters")
public class ParametersDef extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="param_id")
	private Long paramId;
	
	@Column(name="param_name",unique=true,nullable=false)
	private String paramName;
	
	@Column(name="param_desc",nullable=false)
	private String paramDesc;
	
	@Column(name="param_value",nullable=false)
	private String paramValue;
	
	@Column(name="param_active",nullable=false)
	private boolean active;

	public Long getParamId() {
		return paramId;
	}

	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	

}
