package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_end_remarks")
public class EndorsementRemarks {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="remark_id")
	private Long remarkId;
	
	@Column(name="sht_desc",nullable=false)
	private String remarkShtDesc;
	
	@Column(name="remarks",nullable=false)
	private String remarks;

	public Long getRemarkId() {
		return remarkId;
	}

	public void setRemarkId(Long remarkId) {
		this.remarkId = remarkId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarkShtDesc() {
		return remarkShtDesc;
	}

	public void setRemarkShtDesc(String remarkShtDesc) {
		this.remarkShtDesc = remarkShtDesc;
	}
	
	
	

}
