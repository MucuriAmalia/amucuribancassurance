package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;

import javax.persistence.Transient;

public class RevenueItemBean {
	
	private String code;
	
	private String value;
	
	private CoaSubAccounts drAccount;
	
	private CoaSubAccounts crAccount;

	private boolean checked;

	private Long revCode;

	private Long crAccountId;

	private Long drAccountId;

	public Long getDrAccountId() {
		return drAccountId;
	}

	public void setDrAccountId(Long drAccountId) {
		this.drAccountId = drAccountId;
	}

	public Long getCrAccountId() {
		return crAccountId;
	}

	public void setCrAccountId(Long crAccountId) {
		this.crAccountId = crAccountId;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Long getRevCode() {
		return revCode;
	}

	public void setRevCode(Long revCode) {
		this.revCode = revCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RevenueItemBean that = (RevenueItemBean) o;

		if (code != null ? !code.equals(that.code) : that.code != null) return false;
		return value != null ? value.equals(that.value) : that.value == null;

	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RevenueItemBean{" +
				"code='" + code + '\'' +
				", value='" + value + '\'' +
				", checked=" + checked +
				", revCode=" + revCode +
				", crAccountId=" + crAccountId +
				", drAccountId=" + drAccountId +
				'}';
	}
}
