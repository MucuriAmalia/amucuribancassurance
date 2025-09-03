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
@Table(name="sys_brk_prod_subcls")
public class ProductSubclasses extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ps_id")
	private Long psId;
	
	@ManyToOne
	@JoinColumn(name="ps_pr_code",nullable=false)
	private ProductsDef product;
	
	@ManyToOne
	@JoinColumn(name="ps_sub_code",nullable=false)
	private SubClassDef subclass;
	
	@Column(name="ps_mandatory")
	private boolean mandatory;
	
	@Column(name="ps_active")
	private boolean active;


	public Long getPsId() {
		return psId;
	}

	public void setPsId(Long psId) {
		this.psId = psId;
	}

	public ProductsDef getProduct() {
		return product;
	}

	public void setProduct(ProductsDef product) {
		this.product = product;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	

}
