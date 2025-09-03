package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the org_branches database table.
 * 
 */
@Entity
@Table(name="sys_brk_branches",uniqueConstraints={@UniqueConstraint(columnNames={"ob_sht_desc","ob_reg_code"})})
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class OrgBranch extends AuditBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ob_id")
	private Long obId;

	@Column(name="ob_name")
	private String obName;

	
	@Column(name="ob_sht_desc", nullable = false, unique = true)
	private String obShtDesc;

	//bi-directional many-to-one association to Organization
	@XmlTransient
	 @JsonIgnore
	@ManyToOne
	@JoinColumn(name="ob_reg_code")
	private OrgRegions region;
	
	@Column(name="ob_address")
	private String address;
	
	@Column(name="ob_tel_number")
	private String telNumber;
	
	@XmlTransient
	 @JsonIgnore
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="ob_user_code")
	private User branchManager;

	@Column(name = "ob_head_office",length = 1)
	private String headOffice;


	public OrgBranch(String obName, String obShtDesc, OrgRegions region, String address, String telNumber, User branchManager) {
		this.obName = obName;
		this.obShtDesc = obShtDesc;
		this.region = region;
		this.address = address;
		this.telNumber = telNumber;
		this.branchManager = branchManager;
	}

	public OrgBranch() {
	}

	public String getHeadOffice() {
		return headOffice;
	}

	public void setHeadOffice(String headOffice) {
		this.headOffice = headOffice;
	}

	public Long getObId() {
		return this.obId;
	}

	public void setObId(Long obId) {
		this.obId = obId;
	}

	public String getObName() {
		return this.obName;
	}

	public void setObName(String obName) {
		this.obName = obName;
	}

	public String getObShtDesc() {
		return this.obShtDesc;
	}

	public void setObShtDesc(String obShtDesc) {
		this.obShtDesc = obShtDesc;
	}

	public OrgRegions getRegion() {
		return region;
	}

	public void setRegion(OrgRegions region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public User getBranchManager() {
		return branchManager;
	}

	public void setBranchManager(User branchManager) {
		this.branchManager = branchManager;
	}




}