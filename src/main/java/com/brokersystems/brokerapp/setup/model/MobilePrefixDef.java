package com.brokersystems.brokerapp.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="sys_brk_mob_prefix")
public class MobilePrefixDef {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="prefix_id")
	private Long prefixId;
	
	@Column(name="prefix_name",nullable=false,unique=true)
	private String prefixName;

	@ManyToOne
	@JoinColumn(name="prefix_mob_prvdr")
	private MobProviders providers;

	
	@ManyToOne
	@JoinColumn(name="prefix_cou_code",nullable=false)
	private Country country;

	public Long getPrefixId() {
		return prefixId;
	}

	public void setPrefixId(Long prefixId) {
		this.prefixId = prefixId;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public MobProviders getProviders() {
		return providers;
	}

	public void setProviders(MobProviders providers) {
		this.providers = providers;
	}
}
