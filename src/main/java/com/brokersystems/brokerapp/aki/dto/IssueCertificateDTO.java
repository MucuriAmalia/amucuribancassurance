package com.brokersystems.brokerapp.aki.dto;

public class IssueCertificateDTO {
	
	private String TransactionNo;
	private String actualCNo;
	private String Email;
	
	
	public String getTransactionNo() {
		return TransactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		TransactionNo = transactionNo;
	}
	public String getActualCNo() {
		return actualCNo;
	}
	public void setActualCNo(String actualCNo) {
		this.actualCNo = actualCNo;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	
	

}
