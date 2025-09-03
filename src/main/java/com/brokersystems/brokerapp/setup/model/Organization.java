package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the organization database table.
 * 
 */
@Entity
@Table(name="sys_brk_organization")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Organization  implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="org_code")
	private Long orgCode;

	@Column(name="org_desc")
	private String orgDesc;

	@Column(name="org_fax")
	private String orgFax;

	@Column(name="org_logo", length = 200)
	private String orgLogo;

		@Column(name="org_logo_content_type", length = 100)
	private String orgLogoContentType;

	@Column(name="org_mobile")
	private String orgMobile;
	
    
	@Column(name="org_name")
	private String orgName;

	@Column(name="org_phone")
	private String orgPhone;

	@Column(name="org_sht_desc")
	private String orgShtDesc;

	@Column(name="org_website")
	private String orgWebsite;

	
	
	@Column(name="org_date_incorp")
	private Date dateIncorp;
	
	@Column(name="org_cert_number")
	private String certNumber;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="addAddress",column=@Column(name="org_address"))
	})
	private Address address;
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="org_cou_code")
	private Country country;
	 @XmlTransient
	 @JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="org_cur_code")
	private Currencies currency;

	@Column(name="org_pin_number", length = 30)
	private String pinNo;


	public Organization() {
	}


	public String getPinNo() {
		return pinNo;
	}

	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}

	public Long getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(Long orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgDesc() {
		return this.orgDesc;
	}

	public void setOrgDesc(String orgDesc) {
		this.orgDesc = orgDesc;
	}

	public String getOrgFax() {
		return this.orgFax;
	}

	public void setOrgFax(String orgFax) {
		this.orgFax = orgFax;
	}

	public String getOrgLogo() {
		return orgLogo;
	}

	public void setOrgLogo(String orgLogo) {
		this.orgLogo = orgLogo;
	}

	public String getOrgLogoContentType() {
		return orgLogoContentType;
	}

	public void setOrgLogoContentType(String orgLogoContentType) {
		this.orgLogoContentType = orgLogoContentType;
	}

	public String getOrgMobile() {
		return this.orgMobile;
	}

	public void setOrgMobile(String orgMobile) {
		this.orgMobile = orgMobile;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgPhone() {
		return this.orgPhone;
	}

	public void setOrgPhone(String orgPhone) {
		this.orgPhone = orgPhone;
	}

	public String getOrgShtDesc() {
		return this.orgShtDesc;
	}

	public void setOrgShtDesc(String orgShtDesc) {
		this.orgShtDesc = orgShtDesc;
	}

	public String getOrgWebsite() {
		return this.orgWebsite;
	}

	public void setOrgWebsite(String orgWebsite) {
		this.orgWebsite = orgWebsite;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Currencies getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currencies currency) {
		this.currency = currency;
	}
	public Date getDateIncorp() {
		return dateIncorp;
	}

	public void setDateIncorp(Date dateIncorp) {
		this.dateIncorp = dateIncorp;
	}

	public String getCertNumber() {
		return certNumber;
	}

	public void setCertNumber(String certNumber) {
		this.certNumber = certNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	


}