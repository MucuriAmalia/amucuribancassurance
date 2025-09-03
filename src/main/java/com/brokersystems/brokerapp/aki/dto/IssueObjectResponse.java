package com.brokersystems.brokerapp.aki.dto;

import java.util.List;

public class IssueObjectResponse {


    private String Inputs;
    private CertCallBackObj callbackObj;
    private String success;
    private List<AkiErrorLog> Error;
    private String APIRequestNumber;


    public String getInputs() {
        return Inputs;
    }
    public void setInputs(String inputs) {
        Inputs = inputs;
    }
    public CertCallBackObj getCallbackObj() {
        return callbackObj;
    }
    public void setCallbackObj(CertCallBackObj callbackObj) {
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
