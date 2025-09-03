package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;



@Embeddable
public class Address extends AuditBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@Column(name="add_address")
	private String addAddress;
	
	@Column(name="phy_address")
	private String phyAddress;
	
	@Column(name="add_email_addr")
	private String emailAddress;


	public Address() {
	}


	public String getAddAddress() {
		return this.addAddress;
	}

	public void setAddAddress(String addAddress) {
		this.addAddress = addAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public String getPhyAddress() {
		return phyAddress;
	}


	public void setPhyAddress(String phyAddress) {
		this.phyAddress = phyAddress;
	}
	
	
	
	

}