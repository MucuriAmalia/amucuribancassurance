package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_regions",uniqueConstraints={@UniqueConstraint(columnNames={"sht_desc","reg_org_code"})})
public class OrgRegions extends AuditBaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="reg_code")
	private Long regCode;
	
	@Column(name="sht_desc")
	private String shtDesc;
	
	@Column(name="reg_desc")
	private String regDesc;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name="wef")
	private Date regWef;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name="wet")
	private Date regWet;
	
	@XmlTransient
	 @JsonIgnore
	@ManyToOne
	@JoinColumn(name="reg_org_code")
	private Organization organization;

	

	public Long getRegCode() {
		return regCode;
	}

	public void setRegCode(Long regCode) {
		this.regCode = regCode;
	}

	public String getShtDesc() {
		return shtDesc;
	}

	public void setShtDesc(String shtDesc) {
		this.shtDesc = shtDesc;
	}

	public String getRegDesc() {
		return regDesc;
	}

	public void setRegDesc(String regDesc) {
		this.regDesc = regDesc;
	}

	 

	public Date getRegWef() {
		return regWef;
	}

	public void setRegWef(Date regWef) {
		this.regWef = regWef;
	}

	public Date getRegWet() {
		return regWet;
	}

	public void setRegWet(Date regWet) {
		this.regWet = regWet;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	
	
	

}
