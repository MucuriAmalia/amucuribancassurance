package com.brokersystems.brokerapp.aki.dto;

import java.util.List;

public class ReIssueObjectResponse {
	
	private String Inputs;
	private ReissueCertCallBackObj callbackObj;
	private String success;
	private List<AkiErrorLog> Error;
	private String APIRequestNumber;
	
	
	public String getInputs() {
		return Inputs;
	}
	public void setInputs(String inputs) {
		Inputs = inputs;
	}
	public ReissueCertCallBackObj getCallbackObj() {
		return callbackObj;
	}
	public void setCallbackObj(ReissueCertCallBackObj callbackObj) {
		this.callbackObj = callbackObj;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	
	
	
	public List<AkiErrorLog> getError() {
		return Error;
	}
	public void setError(List<AkiErrorLog> error) {
		Error = error;
	}
	public String getAPIRequestNumber() {
		return APIRequestNumber;
	}
	public void setAPIRequestNumber(String aPIRequestNumber) {
		APIRequestNumber = aPIRequestNumber;
	}
	
	

}
