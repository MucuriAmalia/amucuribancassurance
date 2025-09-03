package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_classes")
public class ClassesDef extends AuditBaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cl_id")
	private Long clId;
	
	@Column(name="cl_sht_desc",nullable=false,unique=true)
	private String clShtDesc;
	
	@Column(name="cl_desc",nullable=false)
	private String clDesc;
	
	@Column(name="cl_status",nullable=false)
	private boolean clactive;

	public Long getClId() {
		return clId;
	}

	public void setClId(Long clId) {
		this.clId = clId;
	}

	public String getClShtDesc() {
		return clShtDesc;
	}

	public void setClShtDesc(String clShtDesc) {
		this.clShtDesc = clShtDesc;
	}

	public String getClDesc() {
		return clDesc;
	}

	public void setClDesc(String clDesc) {
		this.clDesc = clDesc;
	}

	public boolean isClactive() {
		return clactive;
	}

	public void setClactive(boolean clactive) {
		this.clactive = clactive;
	}
	
	

}
