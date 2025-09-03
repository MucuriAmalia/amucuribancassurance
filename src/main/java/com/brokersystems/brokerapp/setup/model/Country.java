package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;


/**
 * The persistent class for the countries database table.
 * 
 */
@Entity
@Table(name="sys_brk_countries")
public class Country extends AuditBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="cou_code")
	private Long couCode;

	@Column(name="cou_name",unique=true)
	private String couName;

	@Column(name="cou_sht_desc",unique=true)
	private String couShtDesc;
	
	@Column(name="cou_prefix",unique=true)
	private String prefix;


	public Country() {
	}

	public Long getCouCode() {
		return this.couCode;
	}

	public void setCouCode(Long couCode) {
		this.couCode = couCode;
	}

	public String getCouName() {
		return this.couName;
	}

	public void setCouName(String couName) {
		this.couName = couName;
	}

	public String getCouShtDesc() {
		return this.couShtDesc;
	}

	public void setCouShtDesc(String couShtDesc) {
		this.couShtDesc = couShtDesc;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	

}