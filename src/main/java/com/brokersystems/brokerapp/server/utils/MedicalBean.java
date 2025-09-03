
package com.brokersystems.brokerapp.server.utils;

import java.math.BigDecimal;

public class MedicalBean {
	
	private double coveredAmt;
	private String minAge;
	private String maxAge;
	private String type;
	private String ageBracket;
	private double premium;
	public double getCoveredAmt() {
		return coveredAmt;
	}
	public void setCoveredAmt(double coveredAmt) {
		this.coveredAmt = coveredAmt;
	}
	public String getMinAge() {
		return minAge;
	}
	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}
	public String getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getPremium() {
		return premium;
	}
	public void setPremium(double premium) {
		this.premium = premium;
	}


	public String getAgeBracket() {
		return ageBracket;
	}

	public void setAgeBracket(String ageBracket) {
        this.ageBracket = ageBracket;

    }
	@Override
	public String toString() {
		return "MedicalBean{" +
				"coveredAmt=" + coveredAmt +
				", minAge='" + minAge + '\'' +
				", maxAge='" + maxAge + '\'' +
				", type='" + type + '\'' +
				", premium=" + premium + '}'+"\n";

	}
}
