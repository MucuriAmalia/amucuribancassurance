package com.brokersystems.brokerapp.aki.dto;

public class AkiErrorLog {

	private String errorCode;
	private String errorText;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorText() {
		return errorText;
	}
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return "AkiErrorLog{" +
				"errorCode='" + errorCode + '\'' +
				", errorText='" + errorText + '\'' +
				'}';
	}
}
