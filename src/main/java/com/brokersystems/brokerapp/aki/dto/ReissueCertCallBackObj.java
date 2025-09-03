package com.brokersystems.brokerapp.aki.dto;

public class ReissueCertCallBackObj {

	private IssueCertificateDTO issueCertificate;
	private String IssuanceMessage;

	public String getIssuanceMessage() {
		return IssuanceMessage;
	}

	public void setIssuanceMessage(String issuanceMessage) {
		IssuanceMessage = issuanceMessage;
	}

	public IssueCertificateDTO getIssueCertificate() {
		return issueCertificate;
	}

	public void setIssueCertificate(IssueCertificateDTO issueCertificate) {
		this.issueCertificate = issueCertificate;
	}
	
	
	
}
