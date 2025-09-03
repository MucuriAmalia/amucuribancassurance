package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_postal_codes")
public class PostalCodesDef extends AuditBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="p_code")
	private Long pcode;
	
	@Column(name="p_name", nullable=false)
	private String postalName;
	
	@Column(name="p_zip_code",nullable=false)
	private String zipCode;
	
	//@XmlTransient
	// @JsonIgnore
	@ManyToOne
	@JoinColumn(name="p_town_code")
	//@JsonBackReference
	private Town town;

	public Long getPcode() {
		return pcode;
	}

	public void setPcode(Long pcode) {
		this.pcode = pcode;
	}

	public String getPostalName() {
		return postalName;
	}

	public void setPostalName(String postalName) {
		this.postalName = postalName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
	
	
	
	

}
