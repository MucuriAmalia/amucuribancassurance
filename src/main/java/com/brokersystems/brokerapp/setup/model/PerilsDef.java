package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Entity
@Table(name="sys_brk_perils")
public class PerilsDef {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="p_code")
	private Long perilCode;
	
	@Column(name="p_id",nullable=false)
	private String perilShtDesc;
	
	@Column(name="p_desc",nullable=false)
	private String perilDesc;
	
	@Column(name="p_type")
	private String perilType;

	@Transient
	private Long bindPerilCode;

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy="subclass")
	private List<SubclassPerils> perils;

	public Long getPerilCode() {
		return perilCode;
	}

	public void setPerilCode(Long perilCode) {
		this.perilCode = perilCode;
	}

	public String getPerilShtDesc() {
		return perilShtDesc;
	}

	public void setPerilShtDesc(String perilShtDesc) {
		this.perilShtDesc = perilShtDesc;
	}

	public String getPerilDesc() {
		return perilDesc;
	}

	public void setPerilDesc(String perilDesc) {
		this.perilDesc = perilDesc;
	}

	public String getPerilType() {
		return perilType;
	}

	public void setPerilType(String perilType) {
		this.perilType = perilType;
	}

	public List<SubclassPerils> getPerils() {
		return perils;
	}

	public void setPerils(List<SubclassPerils> perils) {
		this.perils = perils;
	}

	public Long getBindPerilCode() {
		return bindPerilCode;
	}

	public void setBindPerilCode(Long bindPerilCode) {
		this.bindPerilCode = bindPerilCode;
	}
}
