package com.brokersystems.brokerapp.setup.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sys_locales database table.
 * 
 */
@Entity
@Table(name="sys_brk_locales")
public class SysLocale extends AuditBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="loc_id")
	private Long locId;

	@Column(name="loc_desc")
	private String locDesc;

	@Column(name="loc_name")
	private String locName;


	public SysLocale() {
	}

	public Long getLocId() {
		return this.locId;
	}

	public void setLocId(Long locId) {
		this.locId = locId;
	}

	public String getLocDesc() {
		return this.locDesc;
	}

	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}

	public String getLocName() {
		return this.locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}


}