package com.brokersystems.brokerapp.webservices.model;

import java.util.ArrayList;
import java.util.List;

public class DataResponse {

	public DataResponse() {
	}
	private Long totalRecords;
	private List<PolicyModel> policyModels = new ArrayList<>();
	private String message;

	private boolean success;

	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<PolicyModel> getPolicyModels() {
		return policyModels;
	}

	public void setPolicyModels(List<PolicyModel> policyModels) {
		this.policyModels = policyModels;
	}

}
