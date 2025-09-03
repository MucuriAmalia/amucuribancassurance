package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.enums.RevenueItems;

@Entity
@Table(name="sys_brk_revenue_items")
public class RevenueItemsDef extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="rev_id")
	private Long revenueId;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rev_prg_id")
	private ProductGroupDef subClassDef;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="rev_sub_id",nullable=true)
	private SubClassDef prodGroup;
	
	@Column(name="rev_code")
	@Enumerated(EnumType.STRING)
	private RevenueItems item;

	@ManyToOne
	@JoinColumn(name="rev_dr_account")
	private CoaSubAccounts drAccount;

	@Column(name = "rev_applicable_at", length = 1)
	private String applicableAt;


	@ManyToOne
	@JoinColumn(name="rev_cr_account")
	private CoaSubAccounts crAccount;


	public ProductGroupDef getSubClassDef() {
		return subClassDef;
	}

	public String getApplicableAt() {
		return applicableAt;
	}

	public void setApplicableAt(String applicableAt) {
		this.applicableAt = applicableAt;
	}

	public void setSubClassDef(ProductGroupDef subClassDef) {
		this.subClassDef = subClassDef;
	}

	public SubClassDef getProdGroup() {
		return prodGroup;
	}

	public void setProdGroup(SubClassDef prodGroup) {
		this.prodGroup = prodGroup;
	}

	public Long getRevenueId() {
		return revenueId;
	}

	public void setRevenueId(Long revenueId) {
		this.revenueId = revenueId;
	}

	public RevenueItems getItem() {
		return item;
	}

	public void setItem(RevenueItems item) {
		this.item = item;
	}

	public CoaSubAccounts getDrAccount() {
		return drAccount;
	}

	public void setDrAccount(CoaSubAccounts drAccount) {
		this.drAccount = drAccount;
	}

	public CoaSubAccounts getCrAccount() {
		return crAccount;
	}

	public void setCrAccount(CoaSubAccounts crAccount) {
		this.crAccount = crAccount;
	}

	@Override
	public String toString() {
		return "RevenueItemsDef{" +
				", item=" + item +
				'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((revenueId == null) ? 0 : revenueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevenueItemsDef other = (RevenueItemsDef) obj;
		if (revenueId == null) {
			if (other.revenueId != null)
				return false;
		} else if (!revenueId.equals(other.revenueId))
			return false;
		return true;
	}
	
	

}
