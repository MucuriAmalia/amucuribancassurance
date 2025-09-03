package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="sys_sequences")
public class SystemSequence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="seq_id")
	private Long seqId;
	
	@Column(name="seq_prefix",nullable=false)
	private String seqPrefix;
	
	@Column(name="seq_last_count")
	private Long lastNumber;
	
	@Column(name="seq_next_count")
	private Long nextNumber;
	
	@Column(name="seq_type", nullable=false)
	private String seqType;
	
	@Column(name="seq_trans_type")
	private String transType;

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	public String getSeqPrefix() {
		return seqPrefix;
	}

	public void setSeqPrefix(String seqPrefix) {
		this.seqPrefix = seqPrefix;
	}

	public Long getLastNumber() {
		return lastNumber;
	}

	public void setLastNumber(Long lastNumber) {
		this.lastNumber = lastNumber;
	}

	public Long getNextNumber() {
		return nextNumber;
	}

	public void setNextNumber(Long nextNumber) {
		this.nextNumber = nextNumber;
	}

	public String getSeqType() {
		return seqType;
	}

	public void setSeqType(String seqType) {
		this.seqType = seqType;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}
	
	

}
