package com.brokersystems.brokerapp.aki.dto;

public class CertCallBackObj {

	private IssueCertificateDTO issueCertificate;
	private String IssuanceMessage;
	private String IssuanceRequestID;

	public String getIssuanceMessage() {
		return IssuanceMessage;
	}

	public void setIssuanceMessage(String issuanceMessage) {
		IssuanceMessage = issuanceMessage;
	}

	public String getIssuanceRequestID() {
		return IssuanceRequestID;
	}

	public void setIssuanceRequestID(String issuanceRequestID) {
		IssuanceRequestID = issuanceRequestID;
	}

	public IssueCertificateDTO getIssueCertificate() {
		return issueCertificate;
	}

	public void setIssueCertificate(IssueCertificateDTO issueCertificate) {
		this.issueCertificate = issueCertificate;
	}

	@Override
	public String toString() {
		return "CertCallBackObj{" +
				"issueCertificate=" + issueCertificate +
				", IssuanceMessage='" + IssuanceMessage + '\'' +
				", IssuanceRequestID='" + IssuanceRequestID + '\'' +
				'}';
	}
}
